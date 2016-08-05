parser grammar MLSpaceDirectParser;

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
import java.util.Collections;
import java.util.Deque;
import java.util.Set;
// import java.util.LinkedHashMap;
// import java.util.LinkedHashSet;
import java.util.LinkedList;

import org.jamesii.core.util.collection.ListUtils;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.core.math.match.*;
import org.jamesii.core.math.simpletree.*;
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
import model.mlspace.rules.RuleCollection;
import model.mlspace.rules.RuleSide;
import model.mlspace.rules.MLSpaceRule;

}

@members{
private final Map<String, AbstractValueRange<?>> variables = new NonNullMap<String, AbstractValueRange<?>>();
private final Map<String, List<Double>> forLoopVariables = new NonNullMap<String, List<Double>>();
private final Set<String> actuallyUsedVariables = new NonNullSet<String>();
private boolean useExperimentalPostponedRegionInit = false;
private MLSpaceParserHelper parseTool;

private String projectName;
private Map<String,?> varOverrides = Collections.EMPTY_MAP;
private Set<String> localRuleVariables = null;

  public MLSpaceDirectParser(String fileName, Map<String,?> parameters, boolean testFlag) throws java.io.IOException {
    this(new CommonTokenStream(
             new MLSpaceCompositeLexer(
                  new ANTLRFileStream(fileName))),
         testFlag);
    projectName = fileName.substring(fileName.lastIndexOf("/")+1,
                                     fileName.lastIndexOf('.'));
    if (parameters != null) {
        this.varOverrides = parameters;
    }
  }
  
      /**
       * Wrap string in Token stream
       * @param toParse String to parse
       * @return TokenStream
       * @throws IOException
       */
      public static CommonTokenStream stringToTokenStream(String toParse)
          throws IOException {
        return new CommonTokenStream(
                  new MLSpaceCompositeLexer(
                       new ANTLRInputStream(
                            new ByteArrayInputStream(toParse.getBytes()))));
      }
  

  public MLSpaceDirectParser(String toParse, boolean testFlag) throws java.io.IOException {
    this(stringToTokenStream(toParse), testFlag);
  }
  
  public MLSpaceDirectParser(TokenStream input,boolean testFlag) {
    this(input);
    this.parseTool = new MLSpaceParserHelper(testFlag);
  }
  
  
  public Map<String,?> getDefinedConstants() {
    return variables;
  }
  
  public Set<String> getUsedConstants() {
    return actuallyUsedVariables;
  }
  
  Deque<String> forVarNames = new LinkedList<String>();
  Deque<Integer> forVarNextPos = new LinkedList<Integer>();
  Map<String, AbstractValueRange<?>> forVarOverridden = new NonNullMap<String, AbstractValueRange<?>>();

  private void handleForVar(String varName, List<?> varRange) {
    if (varName.equals(forVarNames.peek())) {
      // at least one loop has been run
      int pos = forVarNextPos.pop();
      variables.put(varName,AbstractValueRange.newSingleValue(varRange.get(pos)));
      forVarNextPos.push(++pos);
    } else {
      // variable is new
      if (variables.containsKey(varName)) {
        System.err.println("Variable " + varName
            + " already defined. Previous value "
            + variables.get(varName)
            + " overridden in FOR loop expression by " + varRange);
        forVarOverridden.put(varName, variables.get(varName));
      }
      forVarNames.push(varName);
      forVarNextPos.push(1);
      variables.put(varName,AbstractValueRange.newSingleValue(varRange.size() > 0 ? varRange.get(0) : Double.NaN));
    }
  }
  
  private boolean wasLastLoop(String varName, List<?> varRange) {
    assert varName.equals(forVarNames.peek());
    if (varRange.size() > forVarNextPos.peek())
      return false;
    // else return true, but remove var from internal stack(s) first
    forVarNames.pop();
    forVarNextPos.pop();
    AbstractValueRange<?> prevVal = forVarOverridden.remove(varName);
    if (prevVal != null) variables.put(varName,prevVal);
    else variables.remove(varName);
    return true;
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
        if (vr.size() != 1) return null;
        Object value = vr.iterator().next();
        if (!(value instanceof Number)) return null;
        Double numVal = ((Number) value).doubleValue();
        actuallyUsedVariables.add(varname);
        return numVal;
    }
  }
}

fragment entsep : COMMA | PLUS | DOT;

fullmodel returns [MLSpaceModel model]
@after{$model.setName(projectName);}
: (model_name SEMIC?)?
  variable_defs
  species_defs
 (((init)=> i1=init[false] (SEMIC r1=rules)? {$model = parseTool.getModel($i1.map,$r1.rules,useExperimentalPostponedRegionInit,variables);}) 
| (r2=rules SEMIC i2=init[false] {$model = parseTool.getModel($i2.map,$r2.rules,useExperimentalPostponedRegionInit,variables);}))
  EOF;

model_name : (MODELNAMEKW ID {projectName = $ID.text;});

variable_defs
: (variable_def SEMIC!?)*;

variable_def : ID (BECOMES!|EQ!) n=valset_or_const 
{ Object ovVal = varOverrides.get($ID.text);
  if (ovVal == null)
    variables.put($ID.text,$n.val);
  else if (ovVal instanceof Number) 
      variables.put($ID.text,AbstractValueRange.newSingleValue(((Number) ovVal).doubleValue()));
  else
      variables.put($ID.text,AbstractValueRange.newSingleValue(((String) ovVal)));
  if ($ID.text.equalsIgnoreCase(MLSpaceParserHelper.PERIODIC_BOUNDARIES_SETTING) &&
    parseTool.setPeriodicBoundaries(variables.get($ID.text))!=null) {
    actuallyUsedVariables.add($ID.text);
  }
  if (MLSpaceParserHelper.INSTANT_TRANSFER_ON_INIT.contains($ID.text.toLowerCase()) &&
     Arrays.asList("true", "1", true, 1.0, "yes", "TRUE").containsAll(variables.get($ID.text).toList())) {
       actuallyUsedVariables.add($ID.text);
       useExperimentalPostponedRegionInit = true;
     }
 };

attributes_def returns [Map<String,AbstractValueRange<?>> attMap]
@init {$attMap = new NonNullMap<>();}: 
a1=attribute_def {$attMap.put($a1.name,$a1.val);}
((COMMA!|SEMIC!) a2=attribute_def {$attMap.put($a2.name,$a2.val);})*
;

attribute_def returns [String name, AbstractValueRange<?> val]:
  att=ID {$name = $att.text;} COLON  v=valset_or_const {$val = $v.val;};

valset_or_const returns [AbstractValueRange<?> val]:
 ((interval)=> interval {$val=AbstractValueRange.newInterval($interval.lower,$interval.upper,$interval.incLower,$interval.incUpper);})
|((range)=> range {$val=AbstractValueRange.newRange($range.lower,$range.step,$range.upper);})
|((set)=> set {$val=AbstractValueRange.newSet($set.set);})
|((vector)=> vector {$val=AbstractValueRange.newSingleValue($vector.vec);})
|((numexpr)=> numexpr {$val=AbstractValueRange.newSingleValue($numexpr.val);})
|(ID {if (variables.containsKey($ID.text)) {$val=variables.get($ID.text); actuallyUsedVariables.add($ID.text);} else $val=AbstractValueRange.newSingleValue($ID.text);});   

attributes returns [Map<String,Pair<? extends ValueMatch,String>> attMap]
@init {$attMap = new NonNullMap<>();}: 
a1=attributeWE {$attMap.put($a1.name,new Pair<>($a1.val,$a1.varName));}
(COMMA! a2=attributeWE {$attMap.put($a2.name,new Pair<>($a2.val,$a2.varName));})*
;

attributeWE returns [String name, ValueMatch val,String varName]:
  ({input.LA(2)==BECOMES}?) (vn=ID BECOMES! 
    ((attribute) => attWE=attribute {$name = $attWE.name; $val = $attWE.val; $varName = $vn.text;}
    | an=ID {$name = $an.text; $varName = $vn.text;}))
  | att=attribute {$name = $att.name; $val = $att.val;};

attribute returns [String name, ValueMatch val]:
  att=ID {$name = $att.text;} 
  (vi=var_interval {$val = $vi.val;} 
  |(COLON? v=valset_or_const {$val=new ValueMatchRange($v.val);})
  );

var_interval returns [ValueMatch val]:
  (EQ EQ node {$val = ValueMatches.newEquals($node.node);})
 |(GREATERTHAN EQ node {$val = new RangeMatches.GreaterOrEqual($node.node);})
 |(GREATERTHAN node {$val = new RangeMatches.GreaterThan($node.node);})
 |(LESSTHAN EQ node {$val = new RangeMatches.LessOrEqual($node.node);})
 |(LESSTHAN node {$val = new RangeMatches.LessThan($node.node);})
 |(IN l=(L_PAREN|L_BRACKET) low=node (COMMA|DOTS) up=node r=(R_PAREN|R_BRACKET) 
   {$val = RangeMatches.newInterval($low.node,$l.getType()==L_BRACKET,$up.node,$r.getType()==R_BRACKET);})
  ;

node returns [DoubleNode node]:
  e=multNode  {$node = $e.node;}
        (PLUS e2=multNode {$node = new AddNode($node,$e2.node);}
        |MINUS e2=multNode {$node = new SubtractNode($node,$e2.node);}
        )* ;

multNode returns [DoubleNode node]:
  e=atomNode {$node = $e.node;}
       (TIMES e2=atomNode {$node = new MultNode($node,$e2.node);}
        |DIV e2=atomNode {$node = new DivNode($node,$e2.node);}
       )* ; 
    
atomNode returns [DoubleNode node]  @init{double sign = 1.;}:
  ((MINUS {sign = -1.;} | PLUS)? 
   ( n=numval {$node = new FixedValueNode($n.val);}
   | ID {$node = new VariableNode($ID.text);}
   | (L_PAREN e=node R_PAREN {$node=$e.node;})
   | (L_BRACKET! e=node R_BRACKET! {$node = new IntNode($e.node);})
   | (MIN L_PAREN e1=node COMMA e2=node R_PAREN {$node = new MinNode($e1.node,$e2.node);})   
   | (MAX L_PAREN e1=node COMMA e2=node R_PAREN {$node = new MaxNode($e1.node,$e2.node);})   
  ))
  ( 
    (SQR {$node = new SquareNode($node);}) 
   |(CUB {$node = new PowerNode($node, new FixedValueNode(3.));})
   |(DEGREES {$node = new MultNode($node, new FixedValueNode(Math.PI / 180.));})
   |(POW a=atomNode {$node = new PowerNode($node,$a.node);})
  )? {if (sign==-1) $node = new MinusNode($node);}
  ;
  
interval returns [double lower, double upper,boolean incLower, boolean incUpper]:
  (L_BRACKET! low=numval {$lower = ($low.val);} DOTS! up=numval {$upper = ($up.val);$incLower = true; $incUpper = true;} R_BRACKET!)
	|	 (GREATERTHAN EQ nge = numval {$lower = $nge.val;	$upper = Double.POSITIVE_INFINITY;$incLower = true; $incUpper = true;})
	|	 (LESSTHAN EQ nle = numval {$upper = $nle.val;	$lower = Double.NEGATIVE_INFINITY;$incLower = true; $incUpper = true;})
	|	 (GREATERTHAN ngt = numval {$lower = $ngt.val;	$upper = Double.POSITIVE_INFINITY;$incLower = false; $incUpper = true;})
	|	 (LESSTHAN nlt = numval {$upper = $nlt.val;	$lower = Double.NEGATIVE_INFINITY;$incLower = true; $incUpper = false;})
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
  (numset)=> numset {$set = $numset.set;}
 |idset {$set = $idset.set;};

idset returns [Set<String> set]
@init {$set = new NonNullSet<String>();}:
 L_BRACE! (i1=ID {$set.add($i1.text);} (COMMA! i2=ID {$set.add($i2.text);})+)? R_BRACE!;

numset returns [Set<Double> set]
@init {$set = new NonNullSet<Double>();}:
 L_BRACE! (i1=numexpr {$set.add($i1.val);} (COMMA! i2=numexpr {$set.add($i2.val);})+)? R_BRACE!;

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
	     System.out.println("double value converted to int: " + nval);
	  }; 

numval returns [Double val]: 
    FLOAT {$val = Double.parseDouble($FLOAT.text);}
  | {getSingleNumValFromVar(input.LT(1).getText())!=null}?=> ID
    {$val = getSingleNumValFromVar($ID.text);};
     
species_defs : ((species_def)=> species_def SEMIC?)+;

species_def : ID L_PAREN! spa=attributes_def?  R_PAREN! bindingsitesdef?
 {parseTool.registerSpeciesDef($ID.text,$spa.attMap,$bindingsitesdef.bs);};


bindingsitesdef returns [BindingSites bs]
@init {BindingSites.Builder bsb = new BindingSites.Builder();}:
 LESSTHAN! 
 bsd1=bindingsitedef {bsb.addSite($bsd1.name,$bsd1.relAngle);}
 (COMMA! bsd=bindingsitedef {bsb.addSite($bsd.name,$bsd.relAngle);})*
  GREATERTHAN! {$bs = bsb.build();}; 

bindingsitedef returns [String name, Double relAngle]:
  ID COLON! numexpr {$name = $ID.text; $relAngle = $numexpr.val;};

species returns [String specName]:
{parseTool.isValidSpecies(input.LT(1).getText())}? ID {$specName = $ID.text;};

entities_match[Map<String,String> valExtract] returns [List<RuleEntityWithBindings> list]  
 @init {$list = new ArrayList<RuleEntityWithBindings>();}: 
 (e=entity_match {$list.add($e.ent);} (entsep! e2=entity_match {$list.add($e2.ent);})*)?;

entity_match returns [RuleEntityWithBindings ent]
@init{Species spec = null;}:
  (species)=>species (L_PAREN! atts=attributes? R_PAREN!)?
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
   (op EQ ((ne=numexpr {$val = new ValueModifier.SimpleValueModifier($op.text,$ne.val);})
          |(nn=node {$val = new ValueModifier.TreeValueModifier($op.text,$nn.node);})))
  |((EQ|BECOMES) n=node {$val=new TreeValueModifier($n.node);})
  |(COLON v=valset_or_const {$val=new RangeValueModifier($v.val);});

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
  
rules returns [RuleCollection rules] 
@init{$rules = new RuleCollection();}:
  (rule SEMIC? {$rule.rv != null}? {$rules.add($rule.rv);})+;
  
rule returns [MLSpaceRule rv]:
  lhs=rule_left_hand_side
  ARROW 
  rhs=rule_right_hand_side?  
  AT n=node {$rv=parseTool.parseRule(null,$lhs.lhs,$rhs.context,$rhs.rhs,$n.node,""); /* rule name not supported here */}
  ;
  
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
FOR! for_var {handleForVar($for_var.name,$for_var.range);} 
L_BRACE! init[ignore] 
{if ($map == null) $map = $init.map; else 
 for (Map.Entry<InitEntity,Integer> e: $init.map.entrySet()) {
   $map.put(e.getKey(), e.getValue() + ($map.containsKey(e.getKey()) ? $map.get(e.getKey()) : 0)); 
 }}
R_BRACE! {if (!wasLastLoop($for_var.name,$for_var.range)) input.rewind(mark);}
)+; 


for_var returns [String name, List<?> range]:
ID {$name=$ID.text;} (EQ!|BECOMES!) 
(r=range {$range = AbstractValueRange.newRange($r.lower,$r.step,$r.upper).toList();}  
|set {$range = new ArrayList<Object>($set.set);}
);  

numexpr returns [Double val]:
  e=multExpr {$val = $e.val;}
        (PLUS e=multExpr {$val += $e.val;}
        |MINUS e=multExpr {$val -= $e.val;}
        )* ;

multExpr returns [Double val]:
  e=atomExpr {$val = $e.val;} 
       (TIMES e=atomExpr {$val *= $e.val;}
        |DIV e=atomExpr {$val /= $e.val;}
       )* ; 
    
atomExpr returns [Double val]  @init{double sign = 1.;}:
  (MINUS {sign = -1.;} | PLUS)?
  ( (n=numval {$val=$n.val;})
   |(L_PAREN e=numexpr R_PAREN {$val=$e.val;})
   |(L_BRACKET! e=numexpr R_BRACKET! {$val = (double) ($e.val.intValue());})   
  ) 
  ( 
   (SQR {$val *= $val;}) | (CUB {$val *= $val*$val;})
   |(DEGREES {$val = $val * Math.PI / 180.;})
   |(POW a=atomExpr {$val = Math.pow($val,$a.val);})
  )?
   {if ($val != null) $val *= sign;}
  ;

  
observationTargets returns [List<List<? extends RuleEntity>> obs] @init{$obs = new ArrayList<List<? extends RuleEntity>>();}:
  oTE1=obsTargetEntry {$obs.addAll($oTE1.oe);}
  (SEMIC! oTE2=obsTargetEntry {$obs.addAll($oTE2.oe);})*;

obsTargetEntry returns [List<? extends List<? extends RuleEntity>> oe]
 @init{List<List<? extends RuleEntity>> tmp = new ArrayList<List<? extends RuleEntity>>();} 
 @after{$oe = ListUtils.combinations(tmp);}:
  e1=entities_match[null] {tmp.add($e1.list);} // TODO/CHECK specify attribute to observe as variable to extract?
  (IN! e2=entities_match[null] {tmp.add($e2.list);})*;
  
  
  