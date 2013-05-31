/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.plugins;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.w3c.dom.Element;

/**
 * A unique reference to an XML entity.
 * 
 * @author Mathias Röhl 05.05.06 created
 */
public final class DomId implements IId, Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 5059058393351516334L;

  /** identifier name. */
  private String name;

  /** version. */
  private String version;

  /**
   * The icon uri.
   */
  private URI iconURI;

  /**
   * Instantiates a new dom id.
   */
  public DomId() {
    super();
  }

  /**
   * Construct new Id from a DOM element.
   * 
   * @param idElement
   *          the id element
   */
  public DomId(Element idElement) {
    name = idElement.getAttribute("name");
    version = idElement.getAttribute("version");
    try {
      // fetch the icon URI, if set
      String uri = idElement.getAttribute("icon");

      if ((uri != null) && (uri.compareTo("") != 0)) {
        URL url = getClass().getResource("/" + uri);

        if (url != null) {
          iconURI = url.toURI();
        }

        if (url == null) {
          SimSystem.report(Level.WARNING,
              "Was not able to locate the icon file: " + uri);
        }
      }

    } catch (URISyntaxException e) {
      SimSystem.report(Level.WARNING, "The icon path for the plug-in " + name
          + " is invalid.", e);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (o == null) {
      return false;
    } else if (o instanceof DomId) {
      DomId other = (DomId) o;
      if (name.equals(other.name) && version.equals(other.version)) {
        return true;
      }

      return false;

    } else {
      return false;
    }
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getVersion() {
    return version;
  }

  /**
   * Sets the name.
   * 
   * @param name
   *          the new name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Sets the version.
   * 
   * @param version
   *          the new version
   */
  public void setVersion(String version) {
    this.version = version;
  }

  @Override
  public int hashCode() {
    String composed = name + version;
    return composed.hashCode();
  }

  @Override
  public String toString() {
    return getName() + " " + getVersion();
  }

  @Override
  public URI getIconURI() {
    return iconURI;
  }

  /**
   * Set the icon URI to be used.
   * 
   * @param iconURI
   */
  public void setIconURI(URI iconURI) {
    this.iconURI = iconURI;
  }

  @Override
  public int compareTo(IId o) {
    int result = name.compareTo(o.getName());
    if (result != 0) {
      return result;
    }
    result = version.compareTo(o.getVersion());
    if (result != 0) {
      return result;
    }
    if (iconURI != null) {
      result = iconURI.compareTo(o.getIconURI());
      if (result != 0) {
        return result;
      }
    }
    return 0;
  }

}