parser grammar MLSpaceSmallParser;
// reduced variant of MLSpaceDirectParser not supporting legacy syntax, e.g. attribute:value in assignments (now =)
options {
  backtrack=true;
  output=AST;
  tokenVocab=MLSpaceCompositeLexer;
}
//  superClass=TokenAwareParser;


/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
@header {
package model.mlspace.reader.antlr;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Set;
// import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import org.jamesii.core.util.collection.ArrayMap;
import org.jamesii.core.util.collection.ListUtils;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.core.math.match.*;
import org.jamesii.core.math.simpletree.*;
import org.jamesii.core.math.simpletree.logical.*;
import org.jamesii.core.math.simpletree.mixed.*;
import org.jamesii.core.math.simpletree.nullary.*;
import org.jamesii.core.math.simpletree.binary.*;
import org.jamesii.core.math.simpletree.unary.*;

import model.mlspace.MLSpaceModel;
import model.mlspace.entities.Species;
import model.mlspace.entities.RuleEntity;
import model.mlspace.entities.InitEntity;
import model.mlspace.entities.binding.BindingSites;
import model.mlspace.entities.binding.AllMatchingRuleEntity;
import model.mlspace.entities.binding.InitEntityWithBindings;
import model.mlspace.entities.binding.RuleEntityWithBindings;
import model.mlspace.entities.binding.RuleEntityWithBindings.BindingAction;
import model.mlspace.entities.values.AbstractValueRange;
import model.mlspace.entities.values.ValueMatchRange;
import model.mlspace.reader.*;
import model.mlspace.reader.ValueModifier.*;
import model.mlspace.reader.MLSpaceRuleCreator;
import model.mlspace.rules.RuleCollection;
import model.mlspace.rules.RuleSide;
import model.mlspace.rules.MLSpaceRule;

import org.jamesii.core.util.logging.ApplicationLogger;
import java.util.logging.Level;
}

@members{
private final Map<String, AbstractValueRange<?>> variables = new NonNullMap<>();
private final Map<String, Number> numVariables = new NonNullMap<>();
private final Map<String, List<Double>> forLoopVariables = new NonNullMap<>();
private final Set<String> actuallyUsedVariables = new NonNullSet<>();
private boolean instantTransferOnInit = false;
private MLSpaceParserHelper parseTool;
private boolean varsAllowed;

private String projectName;
private String fileName;
private Map<String,?> varOverrides = Collections.EMPTY_MAP;
private Set<String> localRuleVariables = null;

private static final Collection<String> RANDOM_VECTOR_KEYWORDS = Arrays.asList("rand","Rand","RAND");

  public MLSpaceSmallParser(String fileName, Map<String,?> parameters, boolean testFlag) throws java.io.IOException {
    this(new CommonTokenStream(new MLSpaceCompositeLexer(new ANTLRFileStream(fileName))),testFlag);
    this.projectName = fileName.substring(fileName.lastIndexOf("/")+1,
                                     fileName.lastIndexOf('.'));
    if (parameters != null) { this.varOverrides = parameters; }
    this.fileName = fileName;
  }

  /**
   * Wrap string in Token stream
   * @param toParse String to parse
   * @return TokenStream
   * @throws IOException
   */
  public static CommonTokenStream stringToTokenStream(String toParse) throws IOException {
    return new CommonTokenStream(new MLSpaceCompositeLexer(
             new ANTLRInputStream(new ByteArrayInputStream(toParse.getBytes()))));
      }
  
  public MLSpaceSmallParser(String toParse, boolean testFlag) throws java.io.IOException {
    this(stringToTokenStream(toParse), testFlag);
    this.fileName = " (custom string to parse) " + toParse;
  }
  
  public MLSpaceSmallParser(TokenStream input,boolean testFlag) {
    this(input);
    this.parseTool = new MLSpaceParserHelper(testFlag);
    this.fileName = " (custom token stream to parse)";
  }
  
  
  public Map<String,?> getDefinedConstants() {
    return variables;
  }
  
  public Set<String> getUsedConstants() {
    return actuallyUsedVariables;
  }
  
  private void addVariable(String name, Object val) {
      variables.put(name,AbstractValueRange.newSingleValue(val));
      if (val instanceof Number)
        numVariables.put(name,((Number) val));
  }

  private void addVariable(String name, AbstractValueRange<?> vals) {
      variables.put(name,vals);
      if (vals.size()==1) {
        Object val = vals.getRandomValue(null);
        if (val instanceof Number)
          numVariables.put(name,((Number) val));
      }    
  }

  private void removeVariable(String name) {
      variables.remove(name);
      numVariables.remove(name);
  }
  
  Deque<String> forVarNames = new LinkedList<String>();
  Deque<Integer> forVarNextPos = new LinkedList<Integer>();
  Map<String, AbstractValueRange<?>> forVarOverridden = new NonNullMap<String, AbstractValueRange<?>>();

  /** @return true if range contains no values (and loop should be ignored) */
  private boolean handleForVar(String varName, List<?> varRange) {
    if (varName.equals(forVarNames.peek())) {
      // at least one loop has been run
      int pos = forVarNextPos.pop();
      addVariable(varName,varRange.get(pos));
      forVarNextPos.push(++pos);
      return false;
    }
    if (varRange.isEmpty()) { return true;}
    // variable is new
    if (variables.containsKey(varName)) {
      warnWithLine(Level.SEVERE,"Variable " + varName
          + " already defined. Previous value "
          + variables.get(varName)
          + " overridden in FOR loop expression by " + varRange);
      forVarOverridden.put(varName, variables.get(varName));
    }
    forVarNames.push(varName);
    forVarNextPos.push(1);
    addVariable(varName,varRange.get(0));
    return false;
    
  }
  
  private boolean wasLastLoop(String varName, List<?> varRange) {
    assert varName.equals(forVarNames.peek());
    if (varRange.size() > forVarNextPos.peek())
      return false;
    // else return true, but remove var from internal stack(s) first
    removeLastLoopVar(varName);
    return true;
  }
  
  private void removeLastLoopVar(String varName) {
    String poppedName = forVarNames.pop();
    assert poppedName.equals(varName);
    forVarNextPos.pop();
    AbstractValueRange<?> prevVal = forVarOverridden.remove(varName);
    if (prevVal != null) addVariable(varName,prevVal);
      else removeVariable(varName);
  }
  
  private Double getSingleNumValFromVar(String varname) {
    switch (varname) {
      case "PI":
      case "pi": return Math.PI;
      case "E":
      case "e": return Math.E;
      default:
        AbstractValueRange<?> vr = variables.get(varname);
        if (vr == null) return null;
        Double numVal;
        if (vr.size() == 1) { 
        Object value = vr.iterator().next();
        if (!(value instanceof Number)) return null;
        numVal = ((Number) value).doubleValue();
        } else {
          numVal = parseTool.getRandomValue(vr);
        }
        actuallyUsedVariables.add(varname);
        return numVal;
    }
  }
  
  @Override
  public void emitErrorMessage(String msg) {
    ApplicationLogger.log(Level.SEVERE, msg, 3);
  }
  
  public void warnWithLine(Level level, String msg) {
    Token token = input.LT(-1);
    ApplicationLogger.log(level,
        "in " + this.fileName + " at " + token.getLine() + ":"
            + token.getCharPositionInLine() + " " + token.getText() + "\n" + msg, 1);
  }
  
  private static final List<String> DEFAULT_OBS_ASPECT = Collections.singletonList("#");
  
  private final Collection<String> localRuleVars = new LinkedHashSet<>();
  
  private void addLocalRuleVar(String varName) {
    if (!localRuleVars.add(varName))
      warnWithLine(Level.SEVERE, "Local variable " + varName + " defined (at least) twice in same rule. One value will be ignored.");
  } 

  private void checkLocalRuleVar(String varName) {
    if (localRuleVars.add(varName))
      warnWithLine(Level.SEVERE, "Variable " + varName + " not defined in current rule. Match or assignment will fail.");
  } 

}


fragment entsep : PLUS | DOT;

fullmodel returns [MLSpaceModel model]
@after{$model.setName(projectName);}
: variable_defs
  species_defs
 (((init)=> i1=init[false] (SEMIC r1=rules)? {$model = parseTool.getModel($i1.map,$r1.rules,instantTransferOnInit,variables);}) 
| (r2=rules SEMIC i2=init[false] {$model = parseTool.getModel($i2.map,$r2.rules,instantTransferOnInit,variables);}))
  EOF;

variable_defs
: (variable_def SEMIC!?)*;

variable_def : ID EQ! n=valset_or_const 
{ Object ovVal = varOverrides.get($ID.text);
  if (ovVal == null)
    addVariable($ID.text,$n.val);
  else if (ovVal instanceof Number) 
      addVariable($ID.text,AbstractValueRange.newSingleValue(((Number) ovVal).doubleValue()));
  else
      addVariable($ID.text,AbstractValueRange.newSingleValue(((String) ovVal)));
  if ($ID.text.equalsIgnoreCase(MLSpaceParserHelper.PERIODIC_BOUNDARIES_SETTING) &&
    parseTool.setPeriodicBoundaries(variables.get($ID.text))!=null) {
    actuallyUsedVariables.add($ID.text);
  }
  if (MLSpaceParserHelper.INSTANT_TRANSFER_ON_INIT.contains($ID.text.toLowerCase())) {
    actuallyUsedVariables.add($ID.text);
    if (Arrays.asList("true", "1", true, 1.0, "yes", "TRUE").containsAll(variables.get($ID.text).toList())) {
       instantTransferOnInit = true;
    }
  }  
 };

valset_or_const returns [AbstractValueRange<?> val]:
 ((interval)=> interval {$val=AbstractValueRange.newInterval($interval.lower,$interval.upper,$interval.incLower,$interval.incUpper);})
|((range)=> range {$val=AbstractValueRange.newRange($range.lower,$range.step,$range.upper);})
|((set)=> set {$val=AbstractValueRange.newSet($set.set);})
|((vector)=> vector {$val=AbstractValueRange.newSingleValue($vector.vec);})
|(numexpr {$val=AbstractValueRange.newSingleValue($numexpr.val);})
|(STRING {$val=AbstractValueRange.newSingleValue($STRING.text);})
|(ID {if (variables.containsKey($ID.text)) {$val=variables.get($ID.text); actuallyUsedVariables.add($ID.text);} else $val=AbstractValueRange.newSingleValue($ID.text);});   

attributes_match returns [Map<String,Pair<? extends ValueMatch,String>> attMap]
@init {$attMap = new NonNullMap<>();}: 
a1=attribute_match {$attMap.put($a1.name,new Pair<>($a1.val,$a1.varName));}
(COMMA! a2=attribute_match {$attMap.put($a2.name,new Pair<>($a2.val,$a2.varName));})*
;

attribute_match returns [String name, ValueMatch val,String varName]:
  (L_PAREN varn=ID EQ! attn=ID (vari=var_interval)? R_PAREN 
   {$name=$attn.text;$val=$vari.val; $varName=$varn.text;addLocalRuleVar($varName);})
| (n1=ID ((EQ! att1=ID (vi1=var_interval)? 
            {
             $name=$att1.text;$val=$vi1.val; $varName=$n1.text;addLocalRuleVar($varName);
             if ($val != null)
               warnWithLine(Level.WARNING, "Grouping variable assignment " + $n1.text +"="+$att1.text +" in parenthesis is preferable.\n");
            })
         |(vi2=var_interval {$name=$n1.text;$val=$vi2.val;})))
;
var_interval returns [ValueMatch val]:
  (EQ EQ varexpr {$val = ValueMatches.newEquals($varexpr.node);})
 |(EQ EQ STRING {$val = ValueMatches.newEqualsString($STRING.getText());})
 |(GREATERTHAN EQ varexpr {$val = new RangeMatches.GreaterOrEqual($varexpr.node);})
 |(GREATERTHAN varexpr {$val = new RangeMatches.GreaterThan($varexpr.node);})
 |(LESSTHAN EQ varexpr {$val = new RangeMatches.LessOrEqual($varexpr.node);})
 |(LESSTHAN varexpr {$val = new RangeMatches.LessThan($varexpr.node);})
 |(IN l=(L_PAREN|L_BRACKET) low=varexpr (COMMA|DOTS) up=varexpr r=(R_PAREN|R_BRACKET) 
   {$val = RangeMatches.newInterval($low.node,$l.getType()==L_BRACKET,$up.node,$r.getType()==R_BRACKET);})
  ;

numexpr returns [Double val]:
  {varsAllowed = false;} expr {try {$val=$expr.node.calculateValue(numVariables);} catch (UndefinedVariableException ex) {throw new IllegalStateException(ex);}};

varexpr returns [DoubleNode node]:
  {varsAllowed = true;} expr {$node = $expr.node;};

expr returns [DoubleNode node]:  
  (e=multNode  {$node = $e.node;}
        (PLUS e2=multNode {$node = new AddNode($node,$e2.node);}
        |MINUS e2=multNode {$node = new SubtractNode($node,$e2.node);}
        )*)
 |(IF boolNode THEN et=expr (ELSE ef=expr)? {$node = new IfThenElseNode($boolNode.node,$et.node,$ef.node);})
 ;

multNode returns [DoubleNode node]:
  e=atomNode {$node = $e.node;}
       (TIMES e2=atomNode {$node = new MultNode($node,$e2.node);}
        |DIV e2=atomNode {$node = new DivNode($node,$e2.node);}
       )* ; 
    
atomNode returns [DoubleNode node]  @init{double sign = 1.;}:
  ((MINUS {sign = -1.;} | PLUS)? 
   (n=numval {$node = new FixedValueNode($n.val);}
   |{varsAllowed}? ID {checkLocalRuleVar($ID.text); $node = new VariableNode($ID.text);}
   |(L_PAREN e=expr R_PAREN {$node=$e.node;})
   |(L_PAREN boolNode R_PAREN {$node=new SwitchNode($boolNode.node);})
   |(L_BRACKET! e=expr R_BRACKET! {$node = new IntNode($e.node);})
   |(MIN L_PAREN e1=expr COMMA e2=expr R_PAREN {$node = new MinNode($e1.node,$e2.node);})   
   |(MAX L_PAREN e1=expr COMMA e2=expr R_PAREN {$node = new MaxNode($e1.node,$e2.node);})   
  ))
  ( 
    (SQR {$node = new SquareNode($node);}) 
   |(CUB {$node = new PowerNode($node, new FixedValueNode(3.));})
   |(DEGREES {$node = new MultNode($node, new FixedValueNode(Math.PI / 180.));})
   |(POW a=atomNode {$node = new PowerNode($node,$a.node);})
  )? {if (sign==-1) $node = new MinusNode($node);}
  ;
  
boolNode returns [LogicalNode node]:
  el=expr c=compareOp er=expr {$node = Comparisons.newFromCompString($el.node,$c.text,$er.node);}
;  

compareOp: LESSTHAN EQ? | GREATERTHAN EQ? | NOTEQ | EQ EQ;
  
interval returns [double lower, double upper,boolean incLower, boolean incUpper]:
  (L_BRACKET! low=numexpr {$lower = ($low.val);} DOTS! up=numexpr {$upper = ($up.val);$incLower = true; $incUpper = true;} R_BRACKET!)
	|	 (GREATERTHAN EQ nge = numexpr {$lower = $nge.val;	$upper = Double.POSITIVE_INFINITY;$incLower = true; $incUpper = true;})
	|	 (LESSTHAN EQ nle = numexpr {$upper = $nle.val;	$lower = Double.NEGATIVE_INFINITY;$incLower = true; $incUpper = true;})
	|	 (GREATERTHAN ngt = numexpr {$lower = $ngt.val;	$upper = Double.POSITIVE_INFINITY;$incLower = false; $incUpper = true;})
	|	 (LESSTHAN nlt = numexpr {$upper = $nlt.val;	$lower = Double.NEGATIVE_INFINITY;$incLower = true; $incUpper = false;})
  ;

range returns [double lower, double step, double upper]: 
  (one=numexpr COLON! two=numexpr (COLON! three=numexpr)? 
    {$lower = $one.val; 
     if($three.val == null) 
       {$step = 1; $upper = $two.val;}
     else
      {$step = $two.val; $upper = $three.val;}
     }) 
 ;

set returns [Set<?> set]:
  numset {$set = $numset.set;}
 |idset {$set = $idset.set;};

idset returns [Set<String> set]
@init {$set = new NonNullSet<String>();}:
 L_BRACE! (i1=STRING {$set.add($i1.text);} (COMMA! i2=STRING {$set.add($i2.text);})*) R_BRACE!;

numset returns [Set<Double> set]
@init {$set = new NonNullSet<Double>();}:
 L_BRACE! (i1=numexpr {$set.add($i1.val);} (COMMA! i2=numexpr {$set.add($i2.val);})*) R_BRACE!;

vector returns [double[\] vec]
@init {List<Double> tmp = new ArrayList<Double>();}:
L_PAREN! n1=numexpr {tmp.add($n1.val);} (COMMA! n2=numexpr {tmp.add($n2.val);})+ R_PAREN!
{$vec = new double[tmp.size()];
for(int i=0;i<$vec.length;i++) 
$vec[i] = tmp.get(i);
};

intval_or_var returns [int val] :
    numexpr
     {Double nval = $numexpr.val;
	   $val = nval.intValue(); 
	   if (nval - $val != 0.) 
	     warnWithLine(Level.WARNING, "double value converted to int: " + nval);
	  }; 

numval returns [Double val]: 
    INT {$val = Double.parseDouble($INT.text);}
  | FLOAT {$val = Double.parseDouble($FLOAT.text);}
  | {getSingleNumValFromVar(input.LT(1).getText())!=null}?=> ID
    {$val = getSingleNumValFromVar($ID.text);};
     

species_defs : (species_def SEMIC?)+;

species_def : ID L_PAREN! spa=attributes_def?  R_PAREN! bindingsitesdef?
 {parseTool.registerSpeciesDef($ID.text,$spa.attMap,$bindingsitesdef.bs);};

attributes_def returns [Map<String,AbstractValueRange<?>> attMap]
@init {$attMap = new NonNullMap<>();}: 
a1=attribute_def {$attMap.put($a1.name,$a1.val);}
(COMMA! a2=attribute_def {$attMap.put($a2.name,$a2.val);})*
;

attribute_def returns [String name, AbstractValueRange<?> val]:
  att=ID {$name = $att.text;} COLON  v=valset_or_const {$val = $v.val;};

bindingsitesdef returns [BindingSites bs]
@init {BindingSites.Builder bsb = new BindingSites.Builder();}:
 LESSTHAN! 
 bsd1=bindingsitedef {bsb.addSite($bsd1.name,$bsd1.relAngle);}
 (COMMA! bsd=bindingsitedef {bsb.addSite($bsd.name,$bsd.relAngle);})*
  GREATERTHAN! {$bs = bsb.build();}; 

bindingsitedef returns [String name, Double relAngle]:
  ID COLON! {$name = $ID.text;}
  ({input.LT(1).getText().toLowerCase().equals("any")}?=> ID {$relAngle=null;} 
  | numexpr {$relAngle = $numexpr.val;});

species returns [String specName]:
{parseTool.isValidSpecies(input.LT(1).getText())}? ID {$specName = $ID.text;};


rules returns [RuleCollection rules] 
@init{$rules = new RuleCollection();}:
  (rule SEMIC? {$rule.rv != null}? {$rules.add($rule.rv);})+;
  
rule returns [MLSpaceRule rv] @init{localRuleVars.clear();String rateOrProb = ""; String name = null;}:
  (ID COLON! {name = $ID.text;})?
  lhs=rule_left_hand_side
  ARROW 
  rhs=rule_right_hand_side?  
  AT
  ((rpmark) => rpmark {rateOrProb = $rpmark.value;})?
  n=varexpr {$rv=parseTool.parseRule(name,$lhs.lhs,$rhs.context,$rhs.rhs,$n.node,rateOrProb);}
  ;

rpmark returns [String value]:
(ID {MLSpaceRuleCreator.MARKERS.contains($ID.text)}? EQ {$value = $ID.text;});  
  
rule_left_hand_side  returns [RuleSide lhs]
@init{RuleSide.Builder lhsBuilder = new RuleSide.Builder();}
@after{$lhs = lhsBuilder.build();}:
  e1=entity_match {lhsBuilder.addEntity($e1.ent);}
  ((L_BRACKET {lhsBuilder.makeLastContext();}
   |entsep)
   (e2=entity_match {lhsBuilder.addEntity($e2.ent);}
    (entsep en=entity_match {lhsBuilder.addEntity($en.ent);})*)?
   ({lhsBuilder.isContextSet()}? R_BRACKET | {}))?
;

rule_right_hand_side returns [ModEntity context, List<ModEntity> rhs]
@init{ $rhs = new ArrayList<>();}:
  e1=entity_result[false] {$rhs.add($e1.ent);}
  ((L_BRACKET {$context = $rhs.remove(0);} | entsep)
   (e2=entity_result[false] {$rhs.add($e2.ent);}
   (entsep en=entity_result[false] {$rhs.add($en.ent);})*)?
   ({$context != null}? R_BRACKET | {}))?
;

entity_match returns [RuleEntityWithBindings ent]
@init{Species spec = null;}:
  (species)=>species (L_PAREN! atts=attributes_match? R_PAREN!)?
   {parseTool.checkEntityDefPlausibility($species.specName,$atts.attMap);
    spec = parseTool.getSpeciesForName($species.specName);}
  bs=bindingsites?
   {$ent = new RuleEntityWithBindings(spec,$atts.attMap,$bs.map);
    if ($bs.map != null)
    parseTool.validateBindingSites(spec,$bs.map.keySet());
   };

bindingsites returns [Map<String,RuleEntityWithBindings> map] 
@init {$map = new NonNullMap<String,RuleEntityWithBindings>(false,true);}:
 LESSTHAN! 
 bsm1=bindingsite {$map.put($bsm1.name,$bsm1.ent);}
 (COMMA! bsm=bindingsite {$map.put($bsm.name,$bsm.ent);})*
  GREATERTHAN!; 

bindingsite returns [String name, RuleEntityWithBindings ent]:
  ID COLON! 
  (e=entity_match {$name = $ID.text; $ent = $e.ent;} // TODO/CHECK: variable extraction from bound entity?
  |FREE {$name = $ID.text; $ent = null;}
  |OCC {$name = $ID.text; $ent = new AllMatchingRuleEntity(true);})
 ; 
 
entities_result[boolean ignore] returns [List<ModEntity> list]  
 @init {$list = new ArrayList<>();}: 
 (e=entity_result[ignore] {$list.add($e.ent);} (entsep! e2=entity_result[ignore] {$list.add($e2.ent);})*)?;

entity_result[boolean ignore] returns [ModEntity ent]:
  species {$ent = new ModEntity($species.specName);} 
  (L_PAREN! (name=ID m=valmod {$ent.addAttMod($name.text,$m.val);} 
      (COMMA name=ID m=valmod {$ent.addAttMod($name.text,$m.val);})*)? 
   R_PAREN!)?
   {if (!ignore) parseTool.checkEntityDefPlausibility($species.specName,$ent.getAttributes());}
  (ba=bindingactions {$ent.addBindMods($ba.map);})?
;

valmod returns [ValueModifier val]:
  |(PLUS PLUS {$val = new ValueModifier.SimpleValueModifier("+",1.);}) 
  |(MINUS MINUS {$val = new ValueModifier.SimpleValueModifier("-",1.);}) 
  |(op EQ nn=varexpr {$val = new ValueModifier.TreeValueModifier($op.text,$nn.node);})
  |(EQ n=varexpr {$val=new TreeValueModifier($n.node);})
  |(EQ STRING {$val=new StringAssignmentModifier($STRING.getText());})
  |(COLON STRING {$val=new StringAssignmentModifier($STRING.getText());})
  |(COLON v=valset_or_const {$val=new RangeValueModifier($v.val);})
  |(EQ {RANDOM_VECTOR_KEYWORDS.contains(input.LT(1).getText())}? => ID L_PAREN l=varexpr R_PAREN {$val=new RandomVectorModifier($l.node);});

op: PLUS | MINUS | TIMES | DIV;

bindingactions returns [Map<String,BindingAction> map]
 @init{$map = new NonNullMap<String,BindingAction>();}:
 LESSTHAN! 
 bm=bindingaction {$map.put($bm.name,$bm.action);}
 (COMMA bm2=bindingaction {$map.put($bm2.name,$bm2.action);})*
 GREATERTHAN!;
 
bindingaction returns [String name,BindingAction action]:
  ID COLON! {$name = $ID.text;}
  ( BIND {$action = BindingAction.BIND;} 
   |RELEASE {$action = BindingAction.RELEASE;}
   |REPLACE {$action = BindingAction.REPLACE;}
  );
  

init[boolean ignore] returns [Map<InitEntity,Integer> map]:
i1=init_element[ignore] {$map = $i1.map;}
(entsep! in=init_element[ignore] {$map.putAll($in.map);})*;

init_element[boolean ignore] returns [Map<InitEntity,Integer> map]
@init{
  $map = new NonNullMap<InitEntity,Integer>(); 
  InitEntity tmpEnt = null; 
}: 
 (for_each[ignore] {$map = $for_each.map;})
 |(intval_or_var {ignore = ignore || $intval_or_var.val <= 0;}  
    ((e=entity_result[ignore]
      {tmpEnt = MLSpaceParserHelper.modToInitEntity($e.ent);}) 
    |(L_BRACKET! eba=entities_result[ignore] R_BRACKET! 
     {tmpEnt = new InitEntityWithBindings(MLSpaceParserHelper.modToInitEntities($eba.list));})
   ) (L_BRACKET! init[ignore] R_BRACKET!)?  SEMIC!? 
    {if ($init.map != null)
      tmpEnt.updateSubEntities($init.map);
     if (!ignore)
       $map.put(tmpEnt, $intval_or_var.val);
    });

for_each[boolean ignore] returns [Map<InitEntity,Integer> map]
@init{int mark = -1;}:
( {mark = input.mark();}
FOR! for_var {ignore = ignore | handleForVar($for_var.name,$for_var.range);} 
L_BRACE! init[ignore] 
{if ($map == null) $map = $init.map; else 
 for (Map.Entry<InitEntity,Integer> e: $init.map.entrySet()) {
   $map.put(e.getKey(), e.getValue() + ($map.containsKey(e.getKey()) ? $map.get(e.getKey()) : 0)); 
 }}
R_BRACE! {if (ignore) removeLastLoopVar($for_var.name); else if (!wasLastLoop($for_var.name,$for_var.range)) input.rewind(mark);}
)+; 


for_var returns [String name, List<?> range]:
ID {$name=$ID.text;} EQ! 
(r=range {$range = AbstractValueRange.newRange($r.lower,$r.step,$r.upper).toList();}  
|set {$range = new ArrayList<Object>($set.set);}
);  

  
observationTargets returns [Map<List<? extends RuleEntity>,List<String>> obs]
 @init{$obs = new ArrayMap<List<? extends RuleEntity>,List<String>>();}:
  oTE1=obsTargetEntry { for (List<? extends RuleEntity> lst: $oTE1.oe) $obs.put(lst,$oTE1.what);}
  (SEMIC! oTE2=obsTargetEntry { for (List<? extends RuleEntity> lst: $oTE2.oe) $obs.put(lst,$oTE2.what);})*
 ;

obsTargetEntry returns [List<? extends List<? extends RuleEntity>> oe, List<String> what]
 @init{List<List<? extends RuleEntity>> tmp = new ArrayList<List<? extends RuleEntity>>();$what = DEFAULT_OBS_ASPECT;} 
 @after{$oe = ListUtils.combinations(tmp);}:
  e1=obs_matches {tmp.add($e1.list);} // TODO/CHECK specify attribute to observe as variable to extract?
  (IN! e2=obs_matches {tmp.add($e2.list);})*
  (EQ idlist {$what=$idlist.list;})?
  ;
  
obs_matches returns [List<RuleEntityWithBindings> list]  
 @init {$list = new ArrayList<RuleEntityWithBindings>();}: 
 (e=entity_match {$list.add($e.ent);} 
  (COMMA e2=entity_match {$list.add($e2.ent);})*)?;

 
 idlist returns [List<String> list]
 @init {$list = new ArrayList<String>();}:
 (i1=ID {$list.add($i1.text);} | HASH {$list.add("#");}) (COMMA! i2=ID {$list.add($i2.text);} | HASH {$list.add("#");})*; 
 
 