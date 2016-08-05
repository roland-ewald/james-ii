/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace.javamodels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jamesii.core.math.geometry.shapes.AxisAlignedBox;
import org.jamesii.core.math.geometry.shapes.IShape;
import org.jamesii.core.math.geometry.vectors.AVectorFactory;
import org.jamesii.core.math.geometry.vectors.IVectorFactory;
import org.jamesii.core.math.match.ValueMatch;
import org.jamesii.core.math.match.ValueMatches;
import org.jamesii.core.math.simpletree.nullary.FixedValueNode;
import org.jamesii.core.util.collection.CollectionUtils;
import org.jamesii.core.util.misc.Pair;

import model.mlspace.MLSpaceModel;
import model.mlspace.entities.InitEntity;
import model.mlspace.entities.NSMEntity;
import model.mlspace.entities.RuleEntity;
import model.mlspace.entities.Species;
import model.mlspace.entities.spatial.SpatialAttribute;
import model.mlspace.rules.NSMReactionRule;
import model.mlspace.rules.RuleCollection;
import model.mlspace.rules.RuleSide;
import model.mlspace.rules.attributemodification.ChangeToModification;
import model.mlspace.rules.attributemodification.IAttributeModification;
import model.mlspace.rules.attributemodification.SpecialAttributeModification;
import model.mlspace.subvols.Subvol;

/**
 * NSM test model with three subvols, a small one each at the top and the bottom
 * (half cubes) and a cubic middle one, and a single reaction
 * 
 * @author Arne Bittig
 * @date first half 2011
 */
public class NSMReactTestModel extends MLSpaceModel {

  private static final long serialVersionUID = -2047057271867887939L;

  /**
   * 
   * @param ignored
   *          (no) model parameters
   */
  public NSMReactTestModel(final Map<String, ?> ignored) { // NOSONAR:
    // external requirement

    super("NSM reaction test", null, null);
    Species specS = new Species("P1");
    RuleEntity entS = new RuleEntity(specS);
    // , new Pair<String, Object>("diffusion", 3.));
    Map<String, Object> att1 = new LinkedHashMap<>();
    att1.put("a", 1);
    att1.put("b", 0);
    att1.put(SpatialAttribute.DIFFUSION.toString(), 2.);
    Map<String, Object> att2 = new LinkedHashMap<>();
    att2.put("a", 0);
    att2.put("b", 1);
    att2.put("diffusion", 1.0 / 2);
    NSMEntity entSa1 = new NSMEntity(specS, att1);
    NSMEntity entSa2 = new NSMEntity(specS, att2);
    NSMEntity entTa = new NSMEntity(new Species("P2"),
        CollectionUtils.fillMap(new HashMap<String, Object>(1),
            SpatialAttribute.DIFFUSION.toString(), 2.));

    RuleEntity entT = new RuleEntity(entTa.getSpecies());

    NSMReactionRule simpleRule = new NSMReactionRule(null,
        new RuleSide.Builder().addEntity(entS).addEntity(entT).build(),
        Collections.<IAttributeModification> emptyList(),
        Arrays.<List<IAttributeModification>> asList(
            SpecialAttributeModification.CONSUMED,
            SpecialAttributeModification.CONSUMED),
        Collections.<InitEntity> emptyList(), new FixedValueNode(2.0));

    Map<String, Pair<? extends ValueMatch, String>> attrl =
        new LinkedHashMap<>();
    attrl.put("a", new Pair<>(new ValueMatches.EqualsValue(0), (String) null));
    RuleEntity entSr = new RuleEntity(specS, attrl);

    NSMReactionRule modRule = new NSMReactionRule(null,
        new RuleSide.Builder().addEntity(entSr).build(),
        Collections.<IAttributeModification> emptyList(),
        Arrays.asList(Arrays
            .<IAttributeModification> asList(new ChangeToModification("a", 1))),
        Collections.<InitEntity> emptyList(), new FixedValueNode(8.0));
    RuleCollection rules = new RuleCollection();
    rules.add(simpleRule);
    rules.add(modRule);
    setRules(rules);

    List<Subvol> subvols;
    subvols = new ArrayList<>();

    IVectorFactory vf = new AVectorFactory();
    IShape topBox = new AxisAlignedBox(vf.newPositionVector(0.75, 0.),
        vf.newDisplacementVector(0.25, 0.5));
    IShape cenBox = new AxisAlignedBox(vf.newPositionVector(0.0, 0.),
        vf.newDisplacementVector(0.5, 0.5));
    IShape botBox = new AxisAlignedBox(vf.newPositionVector(-0.75, 0.),
        vf.newDisplacementVector(0.25, 0.5));
    Subvol svt = new Subvol(topBox // 0.5
        , null);
    svt.setName("top");
    subvols.add(svt);

    Subvol svc = new Subvol(cenBox // 1.0
        , null);
    svc.setName("cen");
    subvols.add(svc);

    Subvol svb = new Subvol(botBox // 0.5
        , null);
    svb.setName("bot");
    subvols.add(svb);
    svb.updateState(entSa1, 1003);
    svb.updateState(entSa2, 1002);
    svt.updateState(entTa, 2000);

    final double fourByThree = 1. / 0.75;
    svb.addNeighborOneWay(svc, fourByThree * 2.);
    svc.addNeighborOneWay(svb, fourByThree);
    svc.addNeighborOneWay(svt, fourByThree);
    svt.addNeighborOneWay(svc, fourByThree * 2.);
    setSubvolumes(subvols);
  }
}