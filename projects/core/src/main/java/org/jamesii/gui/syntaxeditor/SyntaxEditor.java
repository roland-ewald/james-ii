/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.syntaxeditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Paint;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.jamesii.SimSystem;

// TODO: Auto-generated Javadoc
/**
 * Provides an editor that supports:<br/>
 * <ul>
 * <li>syntax highlighting with the following attributes
 * <ul>
 * <li>color</li>
 * <li>background color</li>
 * <li>font style</li>
 * </ul>
 * </li>
 * <li>text emphasizers (e.g. to visualize problem regions)
 * <ul>
 * <li>underline a region using an arbitrary stroke</li>
 * <li>cross out a region using an arbitrary stroke</li>
 * <li>enclose a region by a rectangle (no multiple lines rectangle)</li>
 * <li>emphasizers can overlap (e.g. cross out a region and underline it at the
 * same time)</li>
 * </ul>
 * </li>
 * <li>line numbering (can be disabled)</li>
 * <li>icons for emphasizers next to line number (can be disabled)</li>
 * <li>annotations bar on the right, provides a fast overview on where the text
 * has emphasizers related to the entire document and it also provides fast
 * access to that region (can be disabled)</li>
 * </ul>
 * It can be used with any kind of input text. The highlighting is done by
 * providing a parser that implements the {@link ILexer} interface, that in
 * return provides tokens for syntax highlighting as well as tokens for text
 * emphasizing (called problem tokens because it was meant to be to emphasize
 * problems but can be used for anything). For each token one can provide a
 * custom styling, that includes custom background color, custom font style and
 * font color as well as for the emphasizing, a stroke, a stroke color and the
 * emphasizing type. This all is provided by an implementation of the
 * {@link ILexerTokenStylizer} interface for both problem and syntax tokens.
 * Additionally to the font style, colors and strokes the stylizers also provide
 * icons and text for problem tokens that are used in annotations bar and the
 * line numbering area. Tokens are represented using the {@link ILexerToken}
 * interface.
 * <p>
 * <b>Example Usage:</b> <code>
 * <pre>
 * import org.jamesii.gui.syntaxeditor.SyntaxEditor;
 * import org.jamesii.gui.syntaxeditor.highlighting.DefaultHighlighter;
 * import org.jamesii.gui.syntaxeditor.highlighting.DefaultHighlighter.Style;
 * 
 * import java.awt.BorderLayout;
 * 
 * import javax.swing.JFrame;
 * import javax.swing.SwingUtilities;
 * 
 * public class SyntaxEditorTest {
 * 
 *   public static void main(String args[]) {
 *     SwingUtilities.invokeLater(new Runnable() {
 * 
 *       public void run() {
 *         JFrame window = new JFrame("SyntaxEditorTest");
 *         window.setSize(640, 480);
 *         window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 * 
 *         window.getContentPane().setLayout(new BorderLayout());
 * 
 *         DefaultHighlighter highlighter = new DefaultHighlighter(
 *             Style.JAVA);
 * 
 *         SyntaxEditor editor = new SyntaxEditor(
 *             highlighter.getLexer(), highlighter.getSyntaxStylizer());
 * 
 *         window.getContentPane().add(editor, BorderLayout.CENTER);
 * 
 *         editor.setShowLineNumbers(true);
 *         editor.setShowIcons(true);
 *         editor.setShowAnnotations(true);
 *         editor
 *             .setText("import org.jamesii.gui.syntaxeditor.SyntaxEditor;\r\n"
 *                 + "import org.jamesii.gui.syntaxeditor.highlighting.DefaultHighlighter;\r\n"
 *                 + "import org.jamesii.gui.syntaxeditor.highlighting.DefaultHighlighter.Style;\r\n"
 *                 + "\r\n"
 *                 + "import java.awt.BorderLayout;\r\n"
 *                 + "\r\n"
 *                 + "import javax.swing.JFrame;\r\n"
 *                 + "import javax.swing.SwingUtilities;\r\n"
 *                 + "\r\n"
 *                 + "public class SyntaxEditorTest {\r\n"
 *                 + "\r\n"
 *                 + "  public static void main(String args[]) {\r\n"
 *                 + "    SwingUtilities.invokeLater(new Runnable() {\r\n"
 *                 + "\r\n"
 *                 + "      @Override\r\n"
 *                 + "      public void run() {\r\n"
 *                 + "        JFrame window = new JFrame(\"SyntaxEditorTest\");\r\n"
 *                 + "        window.setSize(640, 480);\r\n"
 *                 + "        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);\r\n"
 *                 + "\r\n"
 *                 + "        window.getContentPane().setLayout(new BorderLayout());\r\n"
 *                 + "\r\n"
 *                 + "        DefaultHighlighter highlighter = new DefaultHighlighter(\r\n"
 *                 + "            Style.JAVA);\r\n"
 *                 + "\r\n"
 *                 + "        SyntaxEditor editor = new SyntaxEditor(\r\n"
 *                 + "            highlighter.getLexer(), highlighter.getSyntaxStylizer());\r\n" 
 *                 + "\r\n"
 *                 + "        window.getContentPane().add(editor, BorderLayout.CENTER);\r\n"
 *                 + "\r\n"
 *                 + "        editor.setShowLineNumbers(true);\r\n"
 *                 + "        editor.setShowIcons(true);\r\n"
 *                 + "        editor.setShowAnnotations(true);\r\n"
 *                 + "        editor\r\n"
 *                 + "            .setText(\"\");\r\n"
 *                 + "\r\n"
 *                 + "        window.setVisible(true);\r\n"
 *                 + "      }\r\n"
 *                 + "    });\r\n"
 *                 + "  }\r\n"
 *                 + "\r\n"
 *                 + "}\r\n");
 * 
 *         window.setVisible(true);
 *       }
 *     });
 *   }
 * 
 * }
 * </pre>
 * </code> Important here are the following classes:
 * <ul>
 * <li>JavaLexer - which implements {@link ILexer}</li>
 * <li>JavaProblemTokenStylizer - which implements {@link ILexerTokenStylizer}</li>
 * <li>JavaSyntaxTokenStylizer - which implements {@link ILexerTokenStylizer}</li>
 * <li>JavaProblemToken.Type - is an enumeration like this:
 * <code>public enum JavaProblemTokenType {WARNING, ERROR}</code></li>
 * <li>JavaSyntaxToken.Type - is an enumeration like this:
 * <code>public enum JavaSyntaxTokenType {COMMENT, STRING, NUMBER, ...}</code></li>
 * </ul>
 * They provide a concrete implementation of a parser for the java language to
 * support highlighting for that kind of text.
 * 
 * @author Stefan Rybacki
 * @see ILexer
 * @see ILexerTokenStylizer
 * @see ILexerToken
 */
public final class SyntaxEditor extends JComponent implements DocumentListener,
    CaretListener {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 7237924589313091931L;

  /**
   * line numbering bar on the left hand side of the editor viewport
   */
  private LineNumberBar lineNumberingBar;

  /**
   * the annotations bar on the right hand side of the editor viewport
   */
  private AnnotationsBar annotationsBar;

  /**
   * the actual editor
   */
  private JEditorPane editor;

  /**
   * viewport the editor is in
   */
  private JViewport viewport;

  /**
   * kit used for the editor
   */
  private SyntaxEditorKit kit;

  /**
   * The info view.
   */
  private SyntaxEditorInfoView infoView;

  /**
   * Creates a syntax editor using the passed lexer and stylizers.
   * 
   * @param lexer
   *          the lexer to be used for document parsing and also for providing
   *          tokens (syntax and problem)
   * @param syntaxStylizer
   *          a stylizer responsible for styling syntax tokens
   */
  public SyntaxEditor(ILexer lexer,
      ILexerTokenStylizer<ILexerToken> syntaxStylizer) {
    this();
    setHighlighting(lexer, syntaxStylizer);
  }

  /**
   * Instantiates a new syntax editor. No highlighting will be set.
   */
  public SyntaxEditor() {
    super();
    init();
  }

  /**
   * Paint component.
   * 
   * @param g
   *          the g
   */
  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g;

    if (isOpaque()) {
      Paint p = g2d.getPaint();
      g2d.setPaint(new GradientPaint(0, 0, getBackground().brighter(), 0,
          getHeight() - 1, getBackground()));
      g.fillRect(0, 0, getWidth(), getHeight());
      g2d.setPaint(p);
    }
  }

  /**
   * Helper method that initializes the syntax editor component
   */
  private void init() {
    setPreferredSize(new Dimension(320, 240));

    super.setLayout(new BorderLayout());

    // create editor
    editor = new JEditorPane();

    // create scroll pane for editor
    JScrollPane scroll = new JScrollPane(editor);
    viewport = scroll.getViewport();

    scroll.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
    scroll.setViewportBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));

    super.addImpl(scroll, BorderLayout.CENTER, 0);

    // create line number panel and annotations bar
    lineNumberingBar = new LineNumberBar(editor, viewport);
    annotationsBar = new AnnotationsBar(editor, viewport);
    infoView =
        new SyntaxEditorInfoView(editor, "Model Information",
            SwingConstants.NORTH, true);

    super.addImpl(lineNumberingBar, BorderLayout.LINE_START, 1);
    super.addImpl(annotationsBar, BorderLayout.LINE_END, 2);
    super.addImpl(infoView, BorderLayout.PAGE_END, 3);

    setBorder(BorderFactory.createLineBorder(Color.GRAY));

    // hide info view by default
    setShowInfoView(false);

    addDocumentListener(this);
    addCaretListener(this);
  }

  /**
   * Sets the layout.
   * 
   * @param mgr
   *          the new layout
   */
  @Override
  public void setLayout(LayoutManager mgr) {
    throw new UnsupportedOperationException("setLayout() is not supported;");
  }

  /**
   * Adds the impl.
   * 
   * @param comp
   *          the comp
   * @param constraints
   *          the constraints
   * @param index
   *          the index
   */
  @Override
  protected void addImpl(Component comp, Object constraints, int index) {
    throw new UnsupportedOperationException("add() is not supported;");
  }

  /**
   * Sets whether line numbers should be shown or not
   * 
   * @param s
   *          if true line numbers will be shown.
   */
  public void setShowLineNumbers(boolean s) {
    lineNumberingBar.setShowLineNumbers(s);
  }

  /**
   * Sets the editors text.
   * 
   * @param text
   *          the text to set.
   */
  public void setText(String text) {
    editor.getDocument().removeDocumentListener(this);
    editor.setText(text);
    editor.getDocument().addDocumentListener(this);
  }

  /**
   * Gets the editor's text.
   * 
   * @return the editor's text
   */
  public String getText() {
    return editor.getText();
  }

  /**
   * Gets the text in a given range.
   * 
   * @param offs
   *          the start offset of the range
   * @param len
   *          the range's length
   * @return the text in the given range
   * @throws BadLocationException
   */
  public String getText(int offs, int len) throws BadLocationException {
    return editor.getText(offs, len);
  }

  /**
   * Checks if line numbers are shown or not.
   * 
   * @return true, if line numbers are shown
   */
  public boolean isShowLineNumbers() {
    return lineNumberingBar.isShowLineNumbers();
  }

  /**
   * Sets whether icons should be shown next to the line numbers. Those icons
   * are provided by the set problem stylizer.
   * 
   * @param s
   *          if {@code true} icons should be shown next to line numbers
   */
  public void setShowIcons(boolean s) {
    lineNumberingBar.setShowIcons(s);
  }

  /**
   * Checks if icons are shown next to the line number or not.
   * 
   * @return {@code true}, if icons are shown
   */
  public boolean isShowIcons() {
    return lineNumberingBar.isShowIcons();
  }

  /**
   * Sets whether annotations should be shown.
   * 
   * @param s
   *          if {@code true} annotations should be shown
   */
  public void setShowAnnotations(boolean s) {
    annotationsBar.setShowAnnotations(s);
  }

  /**
   * Checks if annotations are activated to be shown.
   * 
   * @return {@code true}, if annotations are shown
   */
  public boolean isShowAnnotations() {
    return annotationsBar.isShowAnnotations();
  }

  /**
   * Adds a document listener.
   * 
   * @param listener
   *          the listener to attach
   */
  public void addDocumentListener(DocumentListener listener) {
    editor.getDocument().addDocumentListener(listener);
  }

  /**
   * Removes a document listener.
   * 
   * @param listener
   *          the listener to remove
   */
  public void removeDocumentListener(DocumentListener listener) {
    editor.getDocument().removeDocumentListener(listener);
  }

  /**
   * Sets the selection color.
   * 
   * @param c
   *          the new selection color
   */
  public void setSelectionColor(Color c) {
    editor.setSelectionColor(c);
  }

  /**
   * Selects text in the given range.
   * 
   * @param from
   *          the from offset
   * @param to
   *          the to offset
   */
  public void select(int from, int to) {
    Rectangle r1;
    Rectangle r2;
    try {
      // scroll so that token is approximately in the middle of
      // the viewport
      r1 = editor.getUI().modelToView(editor, from);
      r2 = editor.getUI().modelToView(editor, to);

      editor.scrollRectToVisible(new Rectangle((r2.x + r1.x) / 2
          - viewport.getWidth() / 2, r1.y - viewport.getHeight() / 2, viewport
          .getWidth(), viewport.getHeight()));

      // select the token to highlight its position
      editor.setCaretPosition(to);
      editor.moveCaretPosition(from);
    } catch (BadLocationException e) {
    }
  }

  /**
   * Sets the highlighting of the syntax editor.
   * 
   * @param lexer
   *          the lexer that is used to generate tokens that can then be used to
   *          highlight using given stylizers
   * @param syntaxStylizer
   *          the stylizer that is used to style syntax tokens produced by the
   *          given lexer
   */
  public void setHighlighting(ILexer lexer,
      ILexerTokenStylizer<ILexerToken> syntaxStylizer) {
    getDocument().removeDocumentListener(this);
    if (kit == null) {
      kit = new SyntaxEditorKit(lexer, syntaxStylizer);
      editor.setEditorKit(kit);
    } else {
      kit.setHighlighting(lexer, syntaxStylizer);
    }
    getDocument().addDocumentListener(this);
  }

  /**
   * Request focus.
   */
  @Override
  public void requestFocus() {
    editor.requestFocus();
  }

  /**
   * Request focus in window.
   * 
   * @return true, if successful
   */
  @Override
  public boolean requestFocusInWindow() {
    return editor.requestFocusInWindow();
  }

  /**
   * Sets the font.
   * 
   * @param font
   *          the new font
   */
  @Override
  public void setFont(Font font) {
    editor.setFont(font);
  }

  /**
   * Removes the.
   * 
   * @param index
   *          the index
   */
  @Override
  public void remove(int index) {
    throw new UnsupportedOperationException("remove() is not supported;");
  }

  /**
   * Removes the all.
   */
  @Override
  public void removeAll() {
    throw new UnsupportedOperationException("removeAll() is not supported;");
  }

  /**
   * Sets the tool tip text.
   * 
   * @param text
   *          the new tool tip text
   */
  @Override
  public void setToolTipText(String text) {
    throw new UnsupportedOperationException(
        "setToolTipText() is not supported; use Stylizers for this instead");
  }

  /**
   * Sets the enabled.
   * 
   * @param enabled
   *          the new enabled
   */
  @Override
  public void setEnabled(boolean enabled) {
    editor.setEnabled(enabled);
  }

  /**
   * Sets whether the text in the editor should be editable or not.
   * 
   * @param editable
   *          if true text will be editable
   */
  public void setEditable(boolean editable) {
    editor.setEditable(editable);
  }

  /**
   * Gets the editors input map. This can be used to add custom mappings to the
   * editor.
   * 
   * @return the editors input map
   */
  public InputMap getEditorInputMap() {
    return editor.getInputMap();
  }

  /**
   * Gets the undo manager.
   * 
   * @return the undo manager
   */
  public JamesUndoManager getUndoManager() {
    return kit.getUndoManager();
  }

  /**
   * Adds an information provider for the editor. This means it provides
   * additional information to specific parts of the current document text by
   * providing info tokens and a custom stylizer for those tokens.
   * 
   * @param provider
   *          the provider
   */
  public void addInfoProvider(IInfoProvider provider) {
    kit.addInfoProvider(provider);
    annotationsBar.addInfoProvider(provider);
    lineNumberingBar.addInfoProvider(provider);
    infoView.addInfoProvider(provider);
  }

  /**
   * Removes a previously added information provider.
   * 
   * @param provider
   *          the provider
   */
  public void removeInfoProvider(IInfoProvider provider) {
    kit.removeInfoProvider(provider);
    annotationsBar.removeInfoProvider(provider);
    lineNumberingBar.removeInfoProvider(provider);
    infoView.removeInfoProvider(provider);
  }

  /**
   * Gets the caret position.
   * 
   * @return the caret position
   */
  public synchronized int getCaretPosition() {
    return editor.getCaretPosition();
  }

  /**
   * Sets the caret position.
   * 
   * @param position
   *          the new caret position
   */
  public synchronized void setCaretPosition(int position) {
    try {
      editor.setCaretPosition(position);
    } catch (Exception e) {
      SimSystem.report(e);
    }
  }

  /**
   * Gets editors document.
   * 
   * @return the document
   */
  public Document getDocument() {
    return editor.getDocument();
  }

  /**
   * @see javax.swing.JTextPane#addCaretListener(CaretListener)
   * @param listener
   *          the listener to add
   */
  public void addCaretListener(CaretListener listener) {
    editor.addCaretListener(listener);
  }

  /**
   * @see javax.swing.JTextPane#removeCaretListener(CaretListener)
   * @param listener
   */
  public void removeCaretListener(CaretListener listener) {
    editor.removeCaretListener(listener);
  }

  @Override
  public void changedUpdate(DocumentEvent e) {
    // notify info provider about document changes
    for (IInfoProvider p : kit.getInfoProviders()) {
      p.contentChanged(new DocumentReader(e.getDocument()),
          editor.getCaretPosition());
    }
  }

  @Override
  public void insertUpdate(DocumentEvent e) {
    changedUpdate(e);
  }

  @Override
  public void removeUpdate(DocumentEvent e) {
    changedUpdate(e);
  }

  @Override
  public void caretUpdate(CaretEvent e) {
    for (IInfoProvider p : kit.getInfoProviders()) {
      p.cursorPosChanged(e.getDot(), new DocumentReader(editor.getDocument()));
    }
  }

  /**
   * Enables or disables the info view
   * 
   * @param b
   *          if <code>true</code> then the info view will be shown
   */
  public void setShowInfoView(boolean b) {
    infoView.setVisible(b);
    revalidate();
    repaint();
  }

  /**
   * Checks if the info view is shown
   * 
   * @return true, if the info view is shown
   */
  public boolean isShowInfoView() {
    return infoView.isVisible();
  }

}
