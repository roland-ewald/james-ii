/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.jamesii.SimSystem;

/**
 * The Class Reflect.
 * 
 * Provides some convenience methods for Java reflection functionality
 * 
 * @author Jan Himmelspach
 * @author Simon Bartels
 */
public final class Reflect {

  /**
   * Execute method with names methodName, by using the given parameters.
   * 
   * Auto determines the parameter types, and then delegates invocation to
   * {@link #executeMethod(Object, String, Class[], Object[])}.
   * 
   * 
   * @param object
   *          the object the method shall be executed of
   * @param methodName
   *          the method name of the method to be executed
   * @param parameters
   *          the parameters assignment to be used for the call
   * 
   * @return the result of the method call, maybe null
   */
  public static Object executeMethod(Object object, String methodName,
      Object[] parameters) {

    return executeMethod(object, methodName, getParameterTypes(parameters),
        parameters);
  }

  /**
   * Determine the types of the parameters.
   * 
   * @param parameters
   * @return
   */
  public static Class<?>[] getParameterTypes(Object[] parameters) {
    // fetch types of parameters
    Class<?>[] parameterTypes = null;
    if (parameters != null) {
      parameterTypes = new Class<?>[parameters.length];
      for (int i = 0; i < parameters.length; i++) {
        parameterTypes[i] = parameters[i].getClass();
      }
    }
    return parameterTypes;
  }

  /**
   * Fetch the method with the given name and types of the class passed.
   * 
   * @param object
   * @param methodName
   * @param parameterTypes
   * @return
   */
  public static Method getMethod(Class<?> object, String methodName,
      Class<?>[] parameterTypes) throws NoSuchMethodException {
    // search local method and execute
    Method localMethod = null;

    // find the method
    try {
      localMethod = object.getMethod(methodName, parameterTypes);
    } catch (SecurityException | NoSuchMethodException e) {
      // take care of this below
    }

    if (localMethod == null) {
      // in case there was no exact match on the method try to
      // find it by checking via isAssignableFrom
      Method[] ms = object.getMethods();
      for (Method mo : ms) {
        // check whether parameter count equals parameter count
        // expected
        Class<?>[] moPt = mo.getParameterTypes();
        if (moPt.length == parameterTypes.length) {
          // do both names equal?
          if (mo.getName().equals(methodName)) {
            // now check whether each parameter passed would be
            // assignable to the parameter expected in the constructor
            boolean assignable = true;
            for (int i = 0; i < moPt.length; i++) {
              assignable =
                  assignable
                      && ((parameterTypes[i] == null && Object.class
                          .isAssignableFrom(moPt[i]))
                          || moPt[i].isAssignableFrom(parameterTypes[i]) || primitiveDataTypeMatch(
                            moPt[i], parameterTypes[i]));
            }
            if (assignable) {
              localMethod = mo;
              break;
            }
          }
        }
      }
      // still null?
      if (localMethod == null) {
        throw new ReflectionException(
            "Invalid method call. The method with name " + methodName
                + " is not accessible on an object of class " + object);
      }
    }
    return localMethod;
  }

  /**
   * Execute the method with name methodName, by using the given parameters.
   * 
   * The number of entries in the parameterTypes and parameters arrays needs to
   * be identical.
   * 
   * Auto determines the method of the object, and then delegates invocation to
   * {@link #executeMethod(Object, Method, Object[])}.
   * 
   * @param object
   *          the object the method shall be executed of
   * @param methodName
   *          the method name of the method to be executed
   * @param parameterTypes
   *          the parameter types of the method to be executed (needed to find
   *          the correct method to be executed)
   * @param parameters
   *          the parameters assignment to be used for the call
   * 
   * @return the result of the method call, maybe null
   */
  public static Object executeMethod(Object object, String methodName,
      Class<?>[] parameterTypes, Object[] parameters) {

    Method m;
    try {
      m = getMethod(object.getClass(), methodName, parameterTypes);
    } catch (NoSuchMethodException e) {
      SimSystem.report(e);
      return null;
    }

    return executeMethod(object, m, parameters);

  }

  /**
   * Checks if methodParamType is primitive and if so whether the
   * requiredParamtype matches. E.g., when methodParamType is double and
   * requiredParamType is Double this method returns true.
   * 
   * @param methodParamType
   *          Parameter type of a method - may be primitive
   * @param requiredParamType
   *          Required parameter type - MUST NOT be primitive (It should be
   *          impossible to transfer with RMI)
   * @return Whether both types match or not.
   */
  private static boolean primitiveDataTypeMatch(Class<?> methodParamType,
      Class<?> requiredParamType) {
    // check on double (most important - on the top)
    boolean b =
        methodParamType.equals(Double.TYPE)
            && requiredParamType.equals(Double.class);

    // byte
    b =
        b || methodParamType.equals(Byte.TYPE)
            && requiredParamType.equals(Byte.class);

    // short
    b =
        b || methodParamType.equals(Short.TYPE)
            && requiredParamType.equals(Short.class);

    // int
    b =
        b || methodParamType.equals(Integer.TYPE)
            && requiredParamType.equals(Integer.class);

    // long
    b =
        b || methodParamType.equals(Long.TYPE)
            && requiredParamType.equals(Long.class);

    // float
    b =
        b || methodParamType.equals(Float.TYPE)
            && requiredParamType.equals(Float.class);

    // boolean
    b =
        b || methodParamType.equals(Boolean.TYPE)
            && requiredParamType.equals(Boolean.class);

    // char
    b =
        b || methodParamType.equals(Character.TYPE)
            && requiredParamType.equals(Character.class);
    return b;
  }

  /**
   * Execute the method on the given object using the given parameters.
   * 
   * @param object
   *          the object the method shall be executed of
   * @param method
   *          the method to be called on "object" with parameters "parameters".
   * @param parameters
   *          the parameters assignment to be used for the call, can be of
   *          length 0 or null if the method to be called has no parameters
   * 
   * @return the result of the method call, maybe null
   */
  public static Object executeMethod(Object object, Method method,
      Object[] parameters) {
    Object result = null;
    // call the method
    try {
      result = method.invoke(object, parameters);
    } catch (Exception e) {
      throw new ReflectionException(
          "Invalid method call. The method with name " + method.getName()
              + " was not callable on object " + object
              + ". Original exception was a " + e.getClass() + " with message "
              + e.getLocalizedMessage(), e);
    }

    return result;

  }

  /**
   * Instantiate a new instance of the specified class even if the constructor
   * is private. If there is no constructor that matches the given parameter
   * types an Exception is thrown.
   * 
   * @param cl
   *          the class of the object to instantiate
   * @param parameters
   *          the parameters the constructor should be called with
   * @return the created object
   * @throws IllegalArgumentException
   *           the illegal argument exception
   * @throws InvocationTargetException
   *           the invocation target exception
   * @throws InstantiationException
   *           the instantiation exception
   * @throws IllegalAccessException
   *           the illegal access exception
   * @throws NoSuchMethodException
   *           the no such method exception
   * @throws SecurityException
   *           the security exception
   */
  /**
   * @param <T>
   * @param cl
   * @param parameters
   * @return instance of object
   * @throws IllegalArgumentException
   * @throws InvocationTargetException
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws SecurityException
   * @throws NoSuchMethodException
   */
  @SuppressWarnings("unchecked")
  public static <T> T instantiate(Class<?> cl, Object... parameters)
      throws InvocationTargetException, InstantiationException,
      IllegalAccessException, NoSuchMethodException {
    // find constructor for parameters
    Class<?> parameterTypes[] = new Class<?>[parameters.length];
    for (int i = 0; i < parameters.length; i++) {
      parameterTypes[i] =
          parameters[i] == null ? null : parameters[i].getClass();
    }

    Constructor<?> c = null;

    try {
      c = cl.getDeclaredConstructor(parameterTypes);
    } catch (NoSuchMethodException e) {
    }

    if (c == null) {
      // in case there was no exact match on the constructor try to
      // find it by checking via isAssignableFrom
      Constructor<?>[] cs = cl.getDeclaredConstructors();
      for (Constructor<?> co : cs) {
        // check whether parameter count equals parameter count
        // expected
        Class<?>[] coPt = co.getParameterTypes();
        if (coPt.length == parameterTypes.length) {
          // now check whether each parameter passed would be
          // assignable to the parameter expected in the constructor
          boolean assignable = true;
          for (int i = 0; i < coPt.length; i++) {
            assignable =
                assignable
                    && ((parameterTypes[i] == null && Object.class
                        .isAssignableFrom(coPt[i])) || coPt[i]
                        .isAssignableFrom(parameterTypes[i]));
          }
          if (assignable) {
            c = co;
            break;
          }
        }
      }
    }

    if (c == null) {
      throw new NoSuchMethodException(
          "Couldn't find constructor with matching parameter types!");
    }

    boolean accessible = c.isAccessible();
    c.setAccessible(true);
    Object o = c.newInstance(parameters);
    c.setAccessible(accessible);
    return (T) o;
  }

  /**
   * Sets the field of the specified object to the specified value even if this
   * field is private.
   * 
   * @param onObject
   *          the object the field is to be changed for
   * @param fieldName
   *          the field that is to be changed
   * @param value
   *          the value that is to be set
   */
  public static void setField(final Object onObject,
      final String fieldName, final Object value) {
    try {
      final Field declaredField =
          onObject.getClass().getDeclaredField(fieldName);

      AccessController.doPrivileged(new PrivilegedAction() {
        @Override
        public Object run() {
          // privileged code goes here, for example:
          boolean accessible = declaredField.isAccessible();
          declaredField.setAccessible(true);

          // set value
          try {
            declaredField.set(onObject, value);
          } catch (IllegalAccessException e) {
            throw new ReflectionException("Failed on setting the value "
                + value + " to the field " + fieldName, e);
          }

          declaredField.setAccessible(accessible);
          return null; // nothing to return
        }
      });

    } catch (SecurityException | IllegalArgumentException e) {
      throw new ReflectionException(e);
    } catch (NoSuchFieldException e) {
      // do nothing
    }
  }

  /**
   * Hidden constructor
   */
  private Reflect() {
  }

}