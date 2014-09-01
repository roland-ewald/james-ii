/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.macos.autotask;

import java.awt.Image;
import java.awt.PopupMenu;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Locale;

import org.jamesii.SimSystem;
import org.jamesii.gui.application.ApplicationManagerManager;
import org.jamesii.gui.application.IWindowManager;
import org.jamesii.gui.application.autotask.IAutoTask;
import org.jamesii.gui.application.resource.BasicResources;
import org.jamesii.gui.application.resource.IconManager;

/**
 * Basic {@link IAutoTask} that attaches to the application menu to react on
 * actions.
 * 
 * @author Stefan Rybacki
 */
class MacOSMenuInitTask implements IAutoTask {

  /**
   * 
   * LICENCE: JAMESLIC
   * 
   * @author Stefan Rybacki
   * 
   */
  private static final class Handler implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
        throws Exception {
      if (method.getName().equals("handleQuit")) {
        ApplicationManagerManager.getApplicationManager().close(false);
      }

      return null;
    }
  }

  @Override
  public void applicationExited(IWindowManager windowManager) {
  }

  @Override
  public void applicationStarted(IWindowManager windowManager) {
    try {
      // only execute if we are actually on a mac system
      String vers =
          System.getProperty("os.name").toLowerCase(Locale.getDefault());

      if (vers.contains("mac")) {
        Class<?> appClass = Class.forName("com.apple.eawt.Application");
        Object app =
            appClass.getMethod("getApplication", (Class<?>[]) null).invoke(
                null, (Object[]) null);

        Class<?> listenerClass =
            Class.forName("com.apple.eawt.ApplicationListener");
        Object listener =
            Proxy.newProxyInstance(this.getClass().getClassLoader(),
                new Class[] { listenerClass }, new Handler());

        appClass.getMethod("addApplicationListener", listenerClass).invoke(app,
            listener);

        appClass.getMethod("removeAboutMenuItem", (Class<?>[]) null).invoke(
            app, (Object[]) null);
        appClass.getMethod("removePreferencesMenuItem", (Class<?>[]) null)
            .invoke(app, (Object[]) null);

        appClass.getMethod("setDockMenu", PopupMenu.class).invoke(app,
            new Object[] { null });

        Image logo = IconManager.getImage(BasicResources.IMAGE_COSA_LOGO);

        appClass.getMethod("setDockIconImage", Image.class).invoke(app, logo);
      }
    } catch (Throwable e) {
      SimSystem.report(e);
    }
  }
}
