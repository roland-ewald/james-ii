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
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import org.jamesii.core.plugins.annotations.Parameter;
import org.jamesii.core.plugins.annotations.Plugin;

/**
 * 
 * @author Stefan Rybacki
 */
@SupportedAnnotationTypes("org.jamesii.core.plugins.annotations.Plugin")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@Plugin
public class PluginAnnotationProcessor extends AbstractProcessor {

  @Override
  public boolean process(Set<? extends TypeElement> annotations,
      RoundEnvironment roundEnv) {

    final Types typeUtils = processingEnv.getTypeUtils();

    for (Element el : roundEnv.getElementsAnnotatedWith(Plugin.class)) {
      try {
        if (el.getKind() == ElementKind.CLASS) {
          TypeElement e = (TypeElement) el;
          Plugin annotation = e.getAnnotation(Plugin.class);
          PackageElement packageElement =
              (PackageElement) e.getEnclosingElement();

          FileObject fo;

          // check whether element is in fact an AbstractFactory
          String af = "org.jamesii.core.factories.Factory";

          String factory = null;
          TypeElement s = e;
          while (s.asType().getKind() != TypeKind.NONE) {
            s = (TypeElement) typeUtils.asElement(s.getSuperclass());
            if (s == null) {
              break;
            }
            if (s.getQualifiedName() != null
                && af.equals(s.getQualifiedName().toString())) {
              factory = e.getQualifiedName().toString();
              break;
            }
          }

          if (factory == null) {
            processingEnv.getMessager()
                .printMessage(
                    Kind.WARNING,
                    e.getQualifiedName()
                        + " does not extend Factory (skipping)", e);
            continue;
          }

          try {
            fo =
                processingEnv.getFiler().createResource(
                    StandardLocation.CLASS_OUTPUT,
                    packageElement.getQualifiedName(),
                    e.getSimpleName() + ".plugin", e);

            try (BufferedWriter bw = new BufferedWriter(fo.openWriter())) {
              bw.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                  + "<plugin xmlns=\"http://www.jamesii.org/plugin\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.jamesii.org/plugin ../core/src/main/java/org/jamesii/core/plugins/plugin.xsd \">\n");
              bw.append(printId(annotation.name(), annotation.version(),
                  annotation.icon(), e.getSimpleName().toString(), typeUtils));
              bw.append(String.format(
                  "  <factory classname=\"%s\"%s%s>\n",
                  factory,
                  "".equals(annotation.icon()) ? "" : String.format(
                      " icon=\"%s\"", annotation.icon()),
                  "".equals(annotation.name()) ? "" : String.format(
                      " name=\"%s\"", annotation.name())));
              for (Parameter p : annotation.parameters()) {
                bw.append(printParameter(p, typeUtils));
              }
              if (!"".equals(annotation.description())) {
                bw.append(String.format("    <description>%s</description>\n",
                    annotation.description()));
              }
              bw.append("  </factory>\n");
              if (!"".equals(annotation.license().uri())) {
                bw.append(String.format("  <license uri=\"%s\"/>\n", annotation
                    .license().uri()));
              }
              bw.append("</plugin>");
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

  public static String printId(String idName, String version, String icon,
      String elementName, Types typeUtils) {
    String name = idName;
    if ("".equals(name)) {
      if (elementName.toLowerCase().endsWith("factory")) {
        name =
            elementName.substring(0, elementName.length() - "factory".length());
      } else {
        name = elementName;
      }
    }
    return String.format("  <id name=\"%s\" version=\"%s\"/>\n", name, version,
        "".equals(icon) ? "" : String.format(" icon=\"%s\"", icon));
  }

  public static String printParameter(Parameter p, Types typeUtils) {
    String type = null;
    try {
      type = p.type().getName();
    } catch (MirroredTypeException ex) {
      TypeElement asElement =
          (TypeElement) typeUtils.asElement(ex.getTypeMirror());
      type = asElement.getQualifiedName().toString();
    }

    String pluginType = null;
    try {
      type = p.pluginType().getName();
    } catch (MirroredTypeException ex) {
      TypeElement asElement =
          (TypeElement) typeUtils.asElement(ex.getTypeMirror());
      pluginType = asElement.getQualifiedName().toString();
    }

    StringBuilder builder = new StringBuilder();

    builder.append(String.format(
        "  <parameter name=\"%s\" required=\"%s\" type=\"%s\"%s%s>\n",
        p.name(),
        p.required() ? "true" : "false",
        type,
        "".equals(p.defaultValue()) ? "" : String.format(
            " defaultValue=\"%s\"", p.defaultValue()),
        Void.class.getName().equals(pluginType) ? "" : String.format(
            " plugintype=\"%s\"", pluginType)));

    if (!"".equals(p.description())) {
      builder.append(String.format("    <description>%s</description>\n",
          p.description()));
    }
    builder.append("  </parameter>\n");

    return builder.toString();
  }

}