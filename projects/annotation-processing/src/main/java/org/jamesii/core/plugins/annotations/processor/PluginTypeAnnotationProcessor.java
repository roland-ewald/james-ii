/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.plugins.annotations.processor;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import org.jamesii.core.plugins.annotations.Parameter;
import org.jamesii.core.plugins.annotations.PluginType;

/**
 * 
 * @author Stefan Rybacki
 */
@SupportedAnnotationTypes("org.jamesii.core.plugins.annotations.PluginType")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class PluginTypeAnnotationProcessor extends AbstractProcessor {

  @Override
  public boolean process(Set<? extends TypeElement> annotations,
      RoundEnvironment roundEnv) {

    final Types typeUtils = processingEnv.getTypeUtils();

    for (Element el : roundEnv.getElementsAnnotatedWith(PluginType.class)) {
      try {
        if (el.getKind() == ElementKind.CLASS) {
          TypeElement e = (TypeElement) el;
          PluginType annotation = e.getAnnotation(PluginType.class);
          PackageElement packageElement =
              (PackageElement) e.getEnclosingElement();

          FileObject fo;

          // check whether element is infact an AbstractFactory
          // FIXME sr137: usually AbstractFactory.class.getName() would do, but
          // for some reason eclipse has problems with that dependency when
          // using
          // this processor (javac and maven don't have that problem though)
          final String af = "org.jamesii.core.factories.AbstractFactory";
          TypeMirror t = e.asType();

          final Map<TypeMirror, TypeMirror> env = new HashMap<>();

          // generate generic type hierarchy down to AbstractFactory and extract
          // BaseFactory
          String baseFactory = t.accept(new TypeVisitorAdapter<String, Void>() {
            @Override
            public String visitDeclared(DeclaredType t, Void p) {
              List<? extends TypeMirror> args = t.getTypeArguments();
              TypeElement el = (TypeElement) typeUtils.asElement(t);
              List<? extends TypeMirror> argNames =
                  ((DeclaredType) el.asType()).getTypeArguments();

              for (int i = 0; i < args.size(); i++) {
                if (env.containsKey(args.get(i))) {
                  env.put(argNames.get(i), env.get(args.get(i)));
                } else {
                  env.put(argNames.get(i), args.get(i));
                }
              }

              // check whether this is AbstractFactory
              if (af.equals(el.getQualifiedName().toString())) {
                // found AbstractFactory in hierarchy now determine Base factory
                TypeMirror bF = env.get(argNames.get(0));
                return ((TypeElement) typeUtils.asElement(bF))
                    .getQualifiedName().toString();
              }

              return ((TypeElement) typeUtils.asElement(t)).getSuperclass()
                  .accept(this, p);
            }
          }, null);

          if (baseFactory == null) {
            processingEnv.getMessager().printMessage(
                Kind.NOTE,
                e.getQualifiedName()
                    + " does not extend AbstractFactory (skipping)", e);
            continue;
          }

          try {
            fo =
                processingEnv.getFiler().createResource(
                    StandardLocation.CLASS_OUTPUT,
                    packageElement.getQualifiedName(),
                    e.getSimpleName() + ".plugintype", e);

            try (BufferedWriter bw = new BufferedWriter(fo.openWriter())) {
              bw.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                  + "<plugintype\n"
                  + "  xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'\n"
                  + "  xmlns='http://www.jamesii.org/plugintype'\n"
                  + "  xsi:schemaLocation='http://www.jamesii.org/plugintype plugintype.xsd'>\n");
              bw.append(PluginAnnotationProcessor.printId(annotation.name(),
                  annotation.version(), annotation.icon(), e.getSimpleName()
                      .toString(), typeUtils));
              bw.append(String.format(
                  "  <abstractfactory>%s</abstractfactory>\n",
                  e.getQualifiedName()));
              bw.append(String.format("  <basefactory>%s</basefactory>\n",
                  baseFactory));
              for (Parameter p : annotation.parameters()) {
                bw.append(PluginAnnotationProcessor
                    .printParameter(p, typeUtils));
              }
              if (!"".equals(annotation.description())) {
                bw.append(String.format("  <description>%s</description>\n",
                    annotation.description()));
              }
              bw.append(String.format("</plugintype>"));
            }
          } catch (IOException ex) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            ex.printStackTrace(ps);
            String output = baos.toString();
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                output);
          }
        }
      } catch (Exception ex) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        ex.printStackTrace(ps);
        String output = baos.toString();
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, output);
      }

    }

    return true;
  }
}