/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.plugins.annotations.processor;

import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.IntersectionType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.NullType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.TypeVisitor;
import javax.lang.model.type.UnionType;
import javax.lang.model.type.WildcardType;

/**
 * @author Stefan Rybacki
 *
 */
class TypeVisitorAdapter<R, P> implements TypeVisitor<R, P> {

  @Override
  public R visit(TypeMirror t, P p) {
    return null;
  }

  @Override
  public R visit(TypeMirror t) {
    return null;
  }

  @Override
  public R visitPrimitive(PrimitiveType t, P p) {
    return null;
  }

  @Override
  public R visitNull(NullType t, P p) {
    return null;
  }

  @Override
  public R visitArray(ArrayType t, P p) {
    return null;
  }

  @Override
  public R visitDeclared(DeclaredType t, P p) {
    return null;
  }

  @Override
  public R visitError(ErrorType t, P p) {
    return null;
  }

  @Override
  public R visitTypeVariable(TypeVariable t, P p) {
    return null;
  }

  @Override
  public R visitWildcard(WildcardType t, P p) {
    return null;
  }

  @Override
  public R visitExecutable(ExecutableType t, P p) {
    return null;
  }

  @Override
  public R visitNoType(NoType t, P p) {
    return null;
  }

  @Override
  public R visitUnknown(TypeMirror t, P p) {
    return null;
  }

  @Override
  public R visitUnion(UnionType t, P p) {
    return null;
  }

  @Override
  public R visitIntersection(IntersectionType arg0, P arg1) {
    throw new UnsupportedOperationException();
  }

}
