package org.eu.bitzone;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.jar.Manifest;
import java.util.zip.GZIPOutputStream;

import javax.swing.JFileChooser;
import javax.swing.UIManager;

import org.apache.lucene.LucenePackage;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.payloads.PayloadHelper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.codecs.Codec;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.CheckIndex;
import org.apache.lucene.index.CheckIndex.Status.SegmentInfoStatus;
import org.apache.lucene.index.CompositeReader;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.DocValues;
import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FieldInfo.IndexOptions;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.IndexCommit;
import org.apache.lucene.index.IndexDeletionPolicy;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.index.IndexGate;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.IndexableFieldType;
import org.apache.lucene.index.LogMergePolicy;
import org.apache.lucene.index.MergePolicy;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.index.SegmentInfoPerCommit;
import org.apache.lucene.index.SegmentInfos;
import org.apache.lucene.index.SegmentReader;
import org.apache.lucene.index.SlowCompositeReaderWrapper;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.index.TermsEnum.SeekStatus;
import org.apache.lucene.index.TieredMergePolicy;
import org.apache.lucene.misc.SweetSpotSimilarity;
import org.apache.lucene.queries.mlt.MoreLikeThis;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.QueryParserBase;
import org.apache.lucene.queryparser.xml.CoreParser;
import org.apache.lucene.queryparser.xml.CorePlusExtensionsParser;
import org.apache.lucene.search.AutomatonQuery;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.ConstantScoreQuery;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.FilteredQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MultiPhraseQuery;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.search.payloads.PayloadNearQuery;
import org.apache.lucene.search.payloads.PayloadTermQuery;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.search.spans.SpanFirstQuery;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanNotQuery;
import org.apache.lucene.search.spans.SpanOrQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.apache.lucene.store.CompoundFileDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.LockFactory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.NumericUtils;
import org.apache.lucene.util.Version;
import org.apache.lucene.util.automaton.Automaton;
import org.apache.lucene.util.automaton.State;
import org.apache.lucene.util.automaton.Transition;
import org.eu.bitzone.application.metadata.ApplicationMetaData;
import org.getopt.luke.AccessibleHitCollector;
import org.getopt.luke.AccessibleTopHitCollector;
import org.getopt.luke.AllHitsCollector;
import org.getopt.luke.BrowserLauncher;
import org.getopt.luke.ClassFinder;
import org.getopt.luke.CountLimitedHitCollector;
import org.getopt.luke.DocReconstructor;
import org.getopt.luke.DocReconstructor.Reconstructed;
import org.getopt.luke.FieldTermCount;
import org.getopt.luke.GrowableStringArray;
import org.getopt.luke.HighFreqTerms;
import org.getopt.luke.IndexInfo;
import org.getopt.luke.IntPair;
import org.getopt.luke.IntervalLimitedCollector;
import org.getopt.luke.KeepAllIndexDeletionPolicy;
import org.getopt.luke.KeepLastIndexDeletionPolicy;
import org.getopt.luke.LimitedException;
import org.getopt.luke.LukePlugin;
import org.getopt.luke.PanelPrintWriter;
import org.getopt.luke.Prefs;
import org.getopt.luke.Progress;
import org.getopt.luke.ProgressNotification;
import org.getopt.luke.Ranges;
import org.getopt.luke.SlowThread;
import org.getopt.luke.TermStats;
import org.getopt.luke.TermVectorMapper;
import org.getopt.luke.Util;
import org.getopt.luke.XMLExporter;
import org.getopt.luke.decoders.BinaryDecoder;
import org.getopt.luke.decoders.DateDecoder;
import org.getopt.luke.decoders.Decoder;
import org.getopt.luke.decoders.NumIntDecoder;
import org.getopt.luke.decoders.NumLongDecoder;
import org.getopt.luke.decoders.SolrDecoder;
import org.getopt.luke.decoders.StringDecoder;
import org.getopt.luke.plugins.ScriptingPlugin;
import org.getopt.luke.xmlQuery.CorePlusExtensionsParserFactory;
import org.getopt.luke.xmlQuery.XmlQueryParserFactory;

import thinlet.FrameLauncher;
import thinlet.Thinlet;

/**
 * This class allows you to browse a <a href="jakarta.apache.org/lucene">Lucene </a> index in several ways - by
 * document, by term, by query, and by most frequent terms.
 *
 * @author Wojtek Padula
 * @author Andrzej Bialecki
 *
 */
public class Leia extends Thinlet implements ClipboardOwner {

  private static final long serialVersionUID = -470469999079073156L;

  public static Version LV = Version.LUCENE_CURRENT;

  private Directory dir = null;
  String pName = null;
  private IndexReader ir = null;
  private AtomicReader ar = null;
  private IndexSearcher is = null;
  private boolean slowAccess = false;
  private List<String> fn = null;
  private String[] idxFields = null;
  private FieldInfos infos = null;
  private IndexInfo idxInfo = null;
  private Map<String, FieldTermCount> termCounts;
  private final List<LukePlugin> plugins = new ArrayList<LukePlugin>();
  private Object errorDlg = null;
  private Object infoDlg = null;
  private Object statmsg = null;
  private Object slowstatus = null;
  private Object slowmsg = null;
  private final Analyzer stdAnalyzer = new StandardAnalyzer(LV);
  // private QueryParser qp = null;
  private boolean readOnly = false;
  private boolean ram = false;
  private boolean keepCommits = false;
  private final boolean multi = false;
  private int tiiDiv = 1;
  private IndexCommit currentCommit = null;
  private Similarity similarity = null;
  private Object lastST;
  private final HashMap<String, Decoder> decoders = new HashMap<String, Decoder>();
  private final Decoder defDecoder = new StringDecoder();

  /** Default salmon theme. */
  public static final int THEME_DEFAULT = 0;
  /** Gray theme. */
  public static final int THEME_GRAY = 1;
  /** Sandstone theme. */
  public static final int THEME_SANDSTONE = 2;
  /** Sky blue theme. */
  public static final int THEME_SKY = 3;
  /** Navy blue reverse theme. */
  public static final int THEME_NAVY = 4;

  /** Theme color contants. */
  public int[][] themes = {
    { 0xece9d0, 0x000000, 0xf5f4f0, 0x919b9a, 0xb0b0b0, 0xeeeeee, 0xb9b9b9, 0xff8080, 0xc5c5dd }, // default
    { 0xe6e6e6, 0x000000, 0xffffff, 0x909090, 0xb0b0b0, 0xededed, 0xb9b9b9, 0x89899a, 0xc5c5dd }, // gray
    { 0xeeeecc, 0x000000, 0xffffff, 0x999966, 0xb0b096, 0xededcb, 0xcccc99, 0xcc6600, 0xffcc66 }, // sandstone
    { 0xf0f0ff, 0x0000a0, 0xffffff, 0x8080ff, 0xb0b0b0, 0xededed, 0xb0b0ff, 0xff0000, 0xfde0e0 }, // sky
    { 0x6375d6, 0xffffff, 0x7f8fdd, 0xd6dff5, 0x9caae5, 0x666666, 0x003399, 0xff3333, 0x666666 } // navy
  };

  private int numTerms = 0;
  private static boolean exitOnDestroy = false;
  private Class[] analyzers = null;

  private String baseDir = null;

  private final Class[] defaultAnalyzers = { SimpleAnalyzer.class, StandardAnalyzer.class,
    StopAnalyzer.class, WhitespaceAnalyzer.class };

  private static final String MSG_NOINDEX = "FAILED: No index, or index is closed. Reopen it.";
  private static final String MSG_READONLY = "FAILED: Read-Only index.";
  private static final String MSG_EMPTY_INDEX = "Index is empty.";
  private static final String MSG_CONV_ERROR =
    "Some values could not be properly represented in this format. "
      + "They are marked in grey and presented as a hex dump.";
  private static final String MSG_LUCENE3828 =
    "Sorry. This functionality doesn't work with Lucene trunk. See LUCENE-3828 for more details.";

  /** Default constructor, loads preferences, initializes plugins and GUI. */
  public Leia() {
    super();
    Prefs.load();
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch (final Exception e) {
    }
    setTheme(Prefs.getInteger(Prefs.P_THEME, THEME_DEFAULT));
    final String fontName = Prefs.getProperty(Prefs.P_FONT_NAME, "SansSerif");
    final String fontSize = Prefs.getProperty(Prefs.P_FONT_SIZE, "12.0");
    float fsize = 12.0f;
    try {
      fsize = Float.parseFloat(fontSize);
    }
    catch (final Exception e) {
    }
    ;
    final Font f = new Font(fontName, Font.PLAIN, (int) fsize);
    setFont(f);
    addComponent(this, "/xml/luke.xml", null, null);
    errorDlg = addComponent(null, "/xml/error.xml", null, null);
    infoDlg = addComponent(null, "/xml/info.xml", null, null);
    statmsg = find("statmsg");
    slowstatus = find("slowstat");
    slowmsg = find(slowstatus, "slowmsg");
    // populate analyzers
    try {
      final Class[] an = ClassFinder.getInstantiableSubclasses(Analyzer.class);
      if (an == null || an.length == 0) {
        System.err.println("No analyzers???");
        analyzers = defaultAnalyzers;
      }
      else {
        analyzers = an;
      }
      final Object cbType = find("cbType");
      populateAnalyzers(cbType);
    }
    catch (final Exception e) {
      e.printStackTrace();
    }
    initSolrTypes();
    loadPlugins();
  }

  private void initSolrTypes() {
    // add Solr field decoders
    final String[] solrTypes = SolrDecoder.getTypes();
    final Object cbDec = find("cbDec");
    final Font f = getFont(cbDec, "font");
    for (final String s : solrTypes) {
      final Object choice = create("choice");
      setFont(choice, f);
      setString(choice, "name", s);
      setString(choice, "text", s);
      add(cbDec, choice);
    }
  }

  /**
   * Set color theme for the UI.
   *
   * @param which one of the predefined themes. For custom themes use
   *          {@link Thinlet#setColors(int, int, int, int, int, int, int, int, int)}.
   */
  public void setTheme(int which) {
    if (which < 0 || which >= themes.length) {
      which = THEME_DEFAULT;
    }
    final int[] t = themes[which];
    setColors(t[0], t[1], t[2], t[3], t[4], t[5], t[6], t[7], t[8]);
    Prefs.setProperty(Prefs.P_THEME, which + "");
  }

  /**
   * Action handler to select color theme.
   *
   * @param menu
   */
  public void actionTheme(final Object menu) {
    final String which = (String) getProperty(menu, "t");
    int t = THEME_DEFAULT;
    try {
      t = Integer.parseInt(which);
    }
    catch (final Exception e) {
    }
    ;
    setTheme(t);
  }

  /**
   * Populate a combobox with the current list of analyzers.
   *
   * @param combo
   */
  public void populateAnalyzers(final Object combo) {
    removeAll(combo);
    final String[] aNames = new String[analyzers.length];
    for (int i = 0; i < analyzers.length; i++) {
      aNames[i] = analyzers[i].getName();
    }
    Arrays.sort(aNames);
    for (int i = 0; i < aNames.length; i++) {
      final Object choice = create("choice");
      setString(choice, "text", aNames[i]);
      add(combo, choice);
      if (i == 0) {
        setString(combo, "text", aNames[i]);
      }
    }
    int lastAnalyzerIdx = 0;
    final String lastAnalyzer = Prefs.getProperty(Prefs.P_ANALYZER);
    if (lastAnalyzer != null) {
      lastAnalyzerIdx = getIndex(combo, lastAnalyzer);
    }
    if (lastAnalyzerIdx < 0) {
      lastAnalyzerIdx = 0;
    }
    setInteger(combo, "selected", lastAnalyzerIdx);
  }

  /**
   * Return an array of available Analyzer implementations.
   *
   * @return
   */
  public Class[] getAnalyzers() {
    return analyzers;
  }

  /**
   * Loads plugins. Plugins are first searched from the CLASSPATH, and then from a plugin list contained in a resource
   * file "/.plugins". The "/.plugins" resource file has a simple format - one fully qualified class name per line.
   * Blank lines and lines starting with '#' are ignored.
   */
  private void loadPlugins() {
    final List pluginClasses = new ArrayList();
    // try to find all plugins
    try {
      final Class classes[] = ClassFinder.getInstantiableSubclasses(LukePlugin.class);
      if (classes != null && classes.length > 0) {
        pluginClasses.addAll(Arrays.asList(classes));
      }
    }
    catch (final Exception e) {
      e.printStackTrace();
    }
    // load plugins declared in the ".plugins" file
    try {
      final InputStream is = getClass().getResourceAsStream("/.plugins");
      if (is != null) {
        final BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;
        while ((line = br.readLine()) != null) {
          if (line.startsWith("#")) {
            continue;
          }
          if (line.trim().equals("")) {
            continue;
          }
          try {
            final Class clazz = Class.forName(line.trim());
            if (clazz.getSuperclass().equals(LukePlugin.class) && !pluginClasses.contains(clazz)) {
              pluginClasses.add(clazz);
            }
          }
          catch (final Throwable x) {
            //
          }
        }
      }
    }
    catch (final Exception e) {
      e.printStackTrace();
    }
    try {
      final StringBuffer errors = new StringBuffer("Unable to load some plugins:");
      boolean failures = false;
      for (int i = 0; i < pluginClasses.size(); i++) {
        try {
          final LukePlugin plugin =
            (LukePlugin) ((Class) pluginClasses.get(i)).getConstructor(new Class[0]).newInstance(
              new Object[0]);
          final String xul = plugin.getXULName();
          if (xul == null) {
            continue;
          }
          final Object ui = parse(xul, plugin);
          plugin.setApplication(this);
          plugin.setMyUi(ui);
          plugins.add(plugin);
        }
        catch (final Exception e) {
          failures = true;
          e.printStackTrace();
          errors.append("\n" + pluginClasses.get(i).toString());
        }
      }
      if (failures) {
        errorMsg(errors.toString());
      }
    }
    catch (final Exception e) {
      e.printStackTrace();
      errorMsg(e.toString());
    }
    if (plugins.size() == 0) {
      return;
    }
    initPlugins();
  }

  /**
   * Create UI for a single plugin.
   *
   * @param tabs parent tabbedpane
   * @param plugin plugin instance
   */
  private void addPluginTab(final Object tabs, final LukePlugin plugin) {
    final Object tab = create("tab");
    setColor(tab, "foreground", new Color(0x006000));
    setString(tab, "text", plugin.getPluginName());
    setFont(tab, getFont().deriveFont(Font.BOLD));
    add(tabs, tab);
    final Object panel = create("panel");
    setInteger(panel, "gap", 2);
    setInteger(panel, "weightx", 1);
    setInteger(panel, "weighty", 1);
    setChoice(panel, "halign", "fill");
    setChoice(panel, "valign", "fill");
    setInteger(panel, "columns", 1);
    add(tab, panel);
    final Object infobar = create("panel");
    setInteger(infobar, "gap", 8);
    setInteger(infobar, "top", 2);
    setInteger(infobar, "bottom", 2);
    setInteger(infobar, "weightx", 1);
    setChoice(infobar, "halign", "fill");
    setColor(infobar, "background", new Color(0xc0f0c0));
    add(panel, infobar);
    final Object label = create("label");
    setString(label, "text", plugin.getPluginInfo());
    add(infobar, label);
    final Object link = create("button");
    setChoice(link, "type", "link");
    setString(link, "text", plugin.getPluginHome());
    putProperty(link, "url", plugin.getPluginHome());
    setMethod(link, "action", "goUrl(this)", infobar, this);
    add(infobar, link);
    add(panel, create("separator"));
    add(panel, plugin.getMyUi());
  }

  /**
   * Return the list of active plugin instances.
   *
   * @return
   */
  public List<LukePlugin> getPlugins() {
    return Collections.unmodifiableList(plugins);
  }

  /**
   * Get an already instantiated plugin, or null if such plugin was not loaded on startup.
   *
   * @param className fully qualified plugin classname
   * @return
   */
  public LukePlugin getPlugin(final String className) {
    for (int i = 0; i < plugins.size(); i++) {
      final Object plugin = plugins.get(i);
      if (plugin.getClass().getName().equals(className)) {
        return (LukePlugin) plugin;
      }
    }
    return null;
  }

  Thread statusThread = null;
  long lastUpdate = 0;
  long statusSleep = 0;

  /**
   * Display a message on the status bar for 5 seconds.
   *
   * @param msg message to display. Too long messages will be truncated by the UI.
   */
  public void showStatus(final String msg) {
    if (statusThread != null && statusThread.isAlive()) {
      setString(statmsg, "text", msg);
      statusSleep = 5000;
    }
    else {
      statusThread = new Thread() {

        @Override
        public void run() {
          statusSleep = 5000;
          setString(statmsg, "text", msg);
          while (statusSleep > 0) {
            try {
              sleep(500);
            }
            catch (final Exception e) {
            }
            ;
            statusSleep -= 500;
          }
          setString(statmsg, "text", "");
        }
      };
      statusThread.start();
    }
  }

  /**
   * As {@link #showStatus(String)} but also sets the "Last search time" label.
   *
   * @param msg
   */
  public void showSearchStatus(final String msg) {
    setString(lastST, "text", msg);
    showStatus(msg);
  }

  long lastSlowUpdate = 0L;
  long lastSlowCounter = 0L;
  Thread slowThread = null;
  long slowSleep = 0;

  public void showSlowStatus(final String msg, final long counter) {
    if (slowThread != null && slowThread.isAlive()) {
      lastSlowCounter += counter;
      setString(slowmsg, "text", msg + " " + lastSlowCounter);
      slowSleep = 5000;
    }
    else {
      slowThread = new Thread() {

        @Override
        public void run() {
          slowSleep = 5000;
          lastSlowCounter = counter;
          setBoolean(slowstatus, "visible", true);
          setString(slowmsg, "text", msg + " " + lastSlowCounter);
          while (slowSleep > 0) {
            try {
              sleep(500);
            }
            catch (final Exception e) {
            }
            ;
            slowSleep -= 500;
          }
          setString(slowmsg, "text", "");
          setBoolean(slowstatus, "visible", false);
        }
      };
      slowThread.start();
    }
  }

  /**
   * Add a Thinlet component from XUL file.
   *
   * @param parent add the new component to this parent
   * @param compView path to the XUL resource
   * @param handlerStr fully qualified classname of the handler to instantiate, or null if the current class will become
   *          the handler
   * @param argv if not null, these arguments will be passed to the appropriate constructor.
   * @return
   */
  public Object addComponent(final Object parent, final String compView, final String handlerStr,
    final Object[] argv) {
    Object res = null;
    Object handler = null;
    try {
      if (handlerStr != null) {
        if (argv == null) {
          handler =
            Class.forName(handlerStr).getConstructor(new Class[] { Thinlet.class })
            .newInstance(new Object[] { this });
        }
        else {
          handler =
            Class.forName(handlerStr).getConstructor(new Class[] { Thinlet.class, Object[].class })
            .newInstance(new Object[] { this, argv });
        }
      }
      if (handler != null) {
        res = parse(compView, handler);
      }
      else {
        res = parse(compView);
      }
      if (parent != null) {
        if (parent instanceof Thinlet) {
          add(res);
        }
        else {
          add(parent, res);
        }
      }
      return res;
    }
    catch (final Exception exc) {
      exc.printStackTrace();
      errorMsg(exc.getMessage());
      return null;
    }
  }

  /**
   * Show a modal error dialog with OK button.
   *
   * @param msg error message
   */
  public void errorMsg(final String msg) {
    final Object fMsg = find(errorDlg, "msg");
    setString(fMsg, "text", msg);
    add(errorDlg);
  }

  /**
   * Show a modal info dialog with OK button.
   *
   * @param msg info message
   */
  public void infoMsg(final String msg) {
    final Object fMsg = find(infoDlg, "msg");
    setString(fMsg, "text", msg);
    add(infoDlg);
  }

  /**
   * Show an "Open Index" dialog.
   */
  public void actionOpen() {
    final Object dialog = addComponent(this, "/xml/lukeinit.xml", null, null);
    final Object path = find(dialog, "path");
    if (this.baseDir != null) {
      setString(path, "text", this.baseDir);
    }
    else {
      setString(path, "text", System.getProperty("user.dir"));
    }
  }

  /**
   * Browse for a directory, and put the selection result in the indicated widget.
   *
   * @param path Thinlet widget to put the result
   */
  public void openBrowse(final Object path) {
    final JFileChooser fd = new JFileChooser();
    fd.setDialogType(JFileChooser.OPEN_DIALOG);
    fd.setDialogTitle("Select Index directory");
    fd.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    fd.setFileHidingEnabled(false);
    final String strPath = getString(path, "text");
    if (strPath != null) {
      strPath.trim();
    }
    if (strPath != null && strPath.length() > 0) {
      fd.setCurrentDirectory(new File(strPath));
    }
    else if (this.baseDir != null) {
      fd.setCurrentDirectory(new File(this.baseDir));
    }
    else {
      fd.setCurrentDirectory(new File(System.getProperty("user.dir")));
    }
    final int res = fd.showOpenDialog(this);
    File iDir = null;
    if (res == JFileChooser.APPROVE_OPTION) {
      iDir = fd.getSelectedFile();
    }
    if (iDir != null && iDir.exists()) {
      if (!iDir.isDirectory()) {
        iDir = iDir.getParentFile();
      }
      setString(path, "text", iDir.toString());
    }
  }

  /**
   * Select an output file name, and put the selection result in the indicated widget.
   *
   * @param path Thinlet widget to put the result
   */
  public void saveBrowse(final Object path, final Object startButton) {
    final JFileChooser fd = new JFileChooser();
    fd.setDialogType(JFileChooser.SAVE_DIALOG);
    fd.setDialogTitle("Select Output File");
    fd.setFileSelectionMode(JFileChooser.FILES_ONLY);
    fd.setFileHidingEnabled(false);
    final String strPath = getString(path, "text");
    if (strPath != null) {
      strPath.trim();
    }
    if (strPath != null && strPath.length() > 0) {
      fd.setCurrentDirectory(new File(strPath));
    }
    else if (this.baseDir != null) {
      fd.setCurrentDirectory(new File(this.baseDir));
    }
    else {
      fd.setCurrentDirectory(new File(System.getProperty("user.dir")));
    }
    final int res = fd.showSaveDialog(this);
    setBoolean(startButton, "enabled", false);
    File iFile = null;
    if (res == JFileChooser.APPROVE_OPTION) {
      iFile = fd.getSelectedFile();
    }
    if (iFile != null) {
      setString(path, "text", iFile.toString());
      setBoolean(startButton, "enabled", true);
    }
  }

  /**
   * Initialize MRU list of indexes in the open index dialog.
   *
   * @param dialog
   */
  public void setupInit(final Object dialog) {
    final Object path = find(dialog, "path");
    syncMRU(path);
  }

  /**
   * Attempt to load the index with parameters specified in the dialog.
   * <p>
   * NOTE: this method is invoked from the UI. If you need to open an index programmatically, you should use
   * {@link #openIndex(String, boolean, boolean, boolean)} instead.
   * </p>
   *
   * @param dialog UI dialog with parameters
   */
  public void openOk(final Object dialog) {
    final Object path = find(dialog, "path");
    pName = getString(path, "text").trim();

    final boolean force = getBoolean(find(dialog, "force"), "selected");
    final boolean noReader = getBoolean(find(dialog, "cbNoReader"), "selected");
    tiiDiv = 1;
    try {
      tiiDiv = Integer.parseInt(getString(find(dialog, "tiiDiv"), "text"));
    }
    catch (final Exception e) {
      e.printStackTrace();
    }
    final Object dirImpl = getSelectedItem(find(dialog, "dirImpl"));
    String dirClass = null;
    if (dirImpl == null) {
      dirClass = FSDirectory.class.getName();
    }
    else {
      final String name = getString(dirImpl, "name");
      if (name == null) {
        dirClass = getString(dirImpl, "text");
      }
      else {
        if (name.equals("fs")) {
          dirClass = FSDirectory.class.getName();
        }
        else if (name.equals("mmap")) {
          dirClass = MMapDirectory.class.getName();
        }
        else if (name.equals("niofs")) {
          dirClass = NIOFSDirectory.class.getName();
        }
      }
    }
    if (pName == null || pName.trim().equals("")) {
      errorMsg("Invalid path.");
      return;
    }
    readOnly = getBoolean(find(dialog, "ro"), "selected");
    ram = getBoolean(find(dialog, "ram"), "selected");
    keepCommits = getBoolean(find(dialog, "cbKeepCommits"), "selected");
    slowAccess = getBoolean(find(dialog, "cbSlowIO"), "selected");
    decoders.clear();
    currentCommit = null;
    Prefs.addToMruList(pName);
    syncMRU(path);
    remove(dialog);
    if (noReader) {
      removeAll();
      addComponent(this, "/xml/luke.xml", null, null);
      try {
        final Directory d = openDirectory(dirClass, pName, false);
        if (DirectoryReader.indexExists(d)) {
          throw new Exception("there is no valid Lucene index in this directory.");
        }
        dir = d;
        initOverview();
        infoMsg("There is no IndexReader - most actions are disabled. "
          + "You can open IndexReader from current Directory using 'Re-Open'");
      }
      catch (final Exception e) {
        errorMsg("ERROR: " + e.toString());
      }
    }
    else {
      openIndex(pName, force, dirClass, readOnly, ram, keepCommits, null, tiiDiv);
    }
  }

  public void actionClose() {
    if (ir != null) {
      try {
        if (is != null) {
          is = null;
        }
        ir.close();
        if (ar != null) {
          ar.close();
        }
        if (dir != null) {
          dir.close();
        }
      }
      catch (final Exception e) {
        e.printStackTrace();
        errorMsg("Close failed: " + e.getMessage());
      }
    }
    ir = null;
    ar = null;
    dir = null;
    is = null;
    removeAll();
    addComponent(this, "/xml/luke.xml", null, null);
    initPlugins();
  }

  public void actionCommit() {
    if (ir == null) {
      showStatus(MSG_NOINDEX);
      return;
    }
    if (!(ir instanceof DirectoryReader)) {
      showStatus("Commit not possible with " + ir.getClass().getSimpleName());
      return;
    }
    if (readOnly) {
      showStatus(MSG_READONLY);
      return;
    }
    try {
      final IndexCommit commit = ((DirectoryReader) ir).getIndexCommit();
      final Map<String, String> userData = commit.getUserData();
      final TreeMap<String, String> ud = new TreeMap<String, String>(userData);
      final Object dialog = addComponent(this, "/xml/commit.xml", null, null);
      putProperty(dialog, "userData", ud);
      _showUserData(dialog);
    }
    catch (final Exception e) {
      errorMsg("Exception retrieving user data: " + e.toString());
    }
  }

  private void _showUserData(final Object dialog) {
    final Object table = find(dialog, "data");
    removeAll(table);
    final Map<String, String> ud = (Map<String, String>) getProperty(dialog, "userData");
    for (final Entry<String, String> e : ud.entrySet()) {
      final Object row = create("row");
      putProperty(row, "key", e.getKey());
      add(table, row);
      Object cell = create("cell");
      setString(cell, "text", e.getKey());
      add(row, cell);
      cell = create("cell");
      setString(cell, "text", e.getValue());
      add(row, cell);
    }
  }

  public void putUserData(final Object dialog) {
    final Object key = find(dialog, "key");
    final Object value = find(dialog, "value");
    final String k = getString(key, "text");
    final String v = getString(value, "text");
    if (k.equals("")) {
      showStatus("Cannot add empty key.");
      return;
    }
    final Map<String, String> ud = (Map<String, String>) getProperty(dialog, "userData");
    ud.put(k, v);
    _showUserData(dialog);
  }

  public void deleteUserData(final Object dialog) {
    final Object table = find(dialog, "data");
    final Map<String, String> ud = (Map<String, String>) getProperty(dialog, "userData");
    final Object[] rows = getSelectedItems(table);
    if (rows == null || rows.length == 0) {
      return;
    }
    for (final Object row : rows) {
      final Object key = getProperty(row, "key");
      ud.remove(key);
    }
    _showUserData(dialog);
  }

  private IndexWriter createIndexWriter() {
    try {
      final IndexWriterConfig cfg = new IndexWriterConfig(LV, new WhitespaceAnalyzer(LV));
      IndexDeletionPolicy policy;
      if (keepCommits) {
        policy = new KeepAllIndexDeletionPolicy();
      }
      else {
        policy = new KeepLastIndexDeletionPolicy();
      }
      cfg.setIndexDeletionPolicy(policy);
      final MergePolicy mp = cfg.getMergePolicy();
      if (mp instanceof LogMergePolicy) {
        ((LogMergePolicy) mp).setUseCompoundFile(IndexGate.preferCompoundFormat(dir));
      }
      else if (mp instanceof TieredMergePolicy) {
        ((TieredMergePolicy) cfg.getMergePolicy()).setUseCompoundFile(IndexGate
          .preferCompoundFormat(dir));
      }
      final IndexWriter iw = new IndexWriter(dir, cfg);
      return iw;
    }
    catch (final Exception e) {
      errorMsg("Error creating IndexWriter: " + e.toString());
      return null;
    }
  }

  private void refreshAfterWrite() throws Exception {
    actionReopen();
  }

  public void commitUserData(final Object dialog) {
    final Map<String, String> userData = (Map<String, String>) getProperty(dialog, "userData");
    remove(dialog);
    if (!(ir instanceof DirectoryReader)) {
      errorMsg("Not possible with " + ir.getClass().getSimpleName());
      return;
    }
    try {
      final IndexWriter iw = createIndexWriter();
      iw.setCommitData(userData);
      iw.commit();
      iw.close();
      refreshAfterWrite();
    }
    catch (final Exception e) {
      errorMsg("Error: " + e.toString());
    }
  }

  public void actionReopen() {
    if (dir == null) {
      return;
    }
    openIndex(pName, false, dir.getClass().getName(), readOnly, ram, keepCommits, currentCommit,
      tiiDiv);
  }

  /**
   * Open indicated index and re-initialize all GUI and plugins.
   *
   * @param pName path to index
   * @param force if true, and the index is locked, unlock it first. If false, and the index is locked, an error will be
   *          reported.
   * @param readOnly open in read-only mode, and disallow modifications.
   */
  public void openIndex(final String name, final boolean force, final String dirImpl,
    final boolean ro, final boolean ramdir, final boolean keepCommits, final IndexCommit point,
    final int tiiDivisor) {
    pName = name;
    readOnly = ro;
    removeAll();
    final File baseFileDir = new File(name);
    this.baseDir = baseFileDir.toString();
    addComponent(this, "/xml/luke.xml", null, null);
    statmsg = find("statmsg");
    if (dir != null) {
      try {
        if (ir != null) {
          ir.close();
        }
        if (ar != null) {
          ar.close();
        }
      }
      catch (final Exception e) {
      }
      ;
      try {
        if (dir != null) {
          dir.close();
        }
      }
      catch (final Exception e) {
      }
      ;
    }
    final ArrayList<Directory> dirs = new ArrayList<Directory>();
    Throwable lastException = null;
    try {
      Directory d = openDirectory(dirImpl, pName, false);
      if (IndexWriter.isLocked(d)) {
        if (!ro) {
          if (force) {
            IndexWriter.unlock(d);
          }
          else {
            errorMsg("Index is locked. Try 'Force unlock' when opening.");
            d.close();
            d = null;
            return;
          }
        }
      }
      boolean existsSingle = false;
      // IR.indexExists doesn't report the cause of error
      try {
        new SegmentInfos().read(d);
        existsSingle = true;
      }
      catch (final Throwable e) {
        e.printStackTrace();
        lastException = e;
        //
      }
      if (!existsSingle) { // try multi
        final File[] files = baseFileDir.listFiles();
        for (final File f : files) {
          if (f.isFile()) {
            continue;
          }
          Directory d1 = openDirectory(dirImpl, f.toString(), false);
          if (IndexWriter.isLocked(d1)) {
            if (!ro) {
              if (force) {
                IndexWriter.unlock(d1);
              }
              else {
                errorMsg("Index is locked. Try 'Force unlock' when opening.");
                d1.close();
                d1 = null;
                return;
              }
            }
          }
          existsSingle = false;
          try {
            new SegmentInfos().read(d1);
            existsSingle = true;
          }
          catch (final Throwable e) {
            lastException = e;
            e.printStackTrace();
          }
          if (!existsSingle) {
            d1.close();
            continue;
          }
          dirs.add(d1);
        }
      }
      else {
        dirs.add(d);
      }

      if (dirs.size() == 0) {
        if (lastException != null) {
          errorMsg("Invalid directory at the location, check console for more information. Last exception:\n"
            + lastException.toString());
        }
        else {
          errorMsg("No valid directory at the location, try another location.\nCheck console for other possible causes.");
        }
        return;
      }

      if (ramdir) {
        showStatus("Loading index into RAMDirectory ...");
        final Directory dir1 = new RAMDirectory();
        final IndexWriterConfig cfg =
          new IndexWriterConfig(Version.LUCENE_40, new WhitespaceAnalyzer(Version.LUCENE_40));
        final IndexWriter iw1 = new IndexWriter(dir1, cfg);
        iw1.addIndexes(dirs.toArray(new Directory[dirs.size()]));
        iw1.close();
        showStatus("RAMDirectory loading done!");
        if (dir != null) {
          dir.close();
        }
        dirs.clear();
        dirs.add(dir1);
      }
      IndexDeletionPolicy policy;
      if (keepCommits) {
        policy = new KeepAllIndexDeletionPolicy();
      }
      else {
        policy = new KeepLastIndexDeletionPolicy();
      }
      final ArrayList<DirectoryReader> readers = new ArrayList<DirectoryReader>();
      for (final Directory dd : dirs) {
        DirectoryReader reader;
        if (tiiDivisor > 1) {
          reader = DirectoryReader.open(dd, tiiDivisor);
        }
        else {
          reader = DirectoryReader.open(dd);
        }
        readers.add(reader);
      }
      if (readers.size() == 1) {
        ir = readers.get(0);
        dir = ((DirectoryReader) ir).directory();
      }
      else {
        ir = new MultiReader(readers.toArray(new IndexReader[readers.size()]));
      }
      is = new IndexSearcher(ir);
      // XXX
      slowAccess = false;
      initOverview();
      initPlugins();
      showStatus("Index successfully open.");
    }
    catch (final Exception e) {
      e.printStackTrace();
      errorMsg(e.getMessage());
      return;
    }
  }

  /**
   * Open a single directory.
   *
   * @param dirImpl fully-qualified class name of Directory implementation, or "FSDirectory" for {@link FSDirectory}
   * @param file index directory
   * @param create if true, create a new directory
   * @return directory implementation
   */
  Class defaultDirImpl = null;

  public Directory openDirectory(final String dirImpl, final String file, final boolean create)
    throws Exception {
    final File f = new File(file);
    if (!f.exists()) {
      throw new Exception("Index directory doesn't exist.");
    }
    Directory res = null;
    if (dirImpl == null || dirImpl.equals(Directory.class.getName())
      || dirImpl.equals(FSDirectory.class.getName())) {
      return FSDirectory.open(f);
    }
    try {
      final Class implClass = Class.forName(dirImpl);
      Constructor<Directory> constr = implClass.getConstructor(File.class);
      if (constr != null) {
        res = constr.newInstance(f);
      }
      else {
        constr = implClass.getConstructor(File.class, LockFactory.class);
        res = constr.newInstance(f, (LockFactory) null);
      }
    }
    catch (final Throwable e) {
      errorMsg("Invalid directory implementation class: " + dirImpl + " " + e);
      return null;
    }
    if (res != null) {
      return res;
    }
    // fall-back to FSDirectory.
    if (res == null) {
      return FSDirectory.open(f);
    }
    return null;
  }

  /**
   * Indicates whether I/O access should be optimized because the index is on a slow medium (e.g. remote).
   *
   * @return true if I/O access is costly and should be minimized
   */
  public boolean isSlowAccess() {
    return slowAccess;
  }

  /**
   * Set whether the I/O access to this index is costly and should be minimized.
   */
  public void setSlowAccess(final boolean slowAccess) {
    this.slowAccess = slowAccess;
    if (slowAccess) {

    }
  }

  /**
   * Initialize plugins. This method should always be called when a new index is open.
   */
  public void initPlugins() {
    final Object pluginsTabs = find("pluginsTabs");
    removeAll(pluginsTabs);
    for (int i = 0; i < plugins.size(); i++) {
      final LukePlugin plugin = plugins.get(i);
      addPluginTab(pluginsTabs, plugin);
      plugin.setDirectory(dir);
      plugin.setReader(ir);
      try {
        plugin.init();
      }
      catch (final Exception e) {
        e.printStackTrace();
        showStatus("PLUGIN ERROR: " + e.getMessage());
      }
    }
  }

  /**
   * Initialize index overview and other GUI elements. This method is called always when a new index is open.
   */
  private void initOverview() {
    try {
      initSolrTypes();
      courier = new Font("Courier", getFont().getStyle(), getFont().getSize());
      lastST = find("lastST");
      setBoolean(find("bReload"), "enabled", true);
      setBoolean(find("bClose"), "enabled", true);
      setBoolean(find("bCommit"), "enabled", true);
      final Object sTable = find("sTable");
      removeAll(sTable);
      final Object docTable = find("docTable");
      removeAll(docTable);
      final Object cbType = find("cbType");
      populateAnalyzers(cbType);
      final Object pOver = find("pOver");
      Object iName = find("idx");
      String idxName;
      if (pName.length() > 40) {
        idxName = pName.substring(0, 10) + "..." + pName.substring(pName.length() - 27);
      }
      else {
        idxName = pName;
      }
      setString(iName, "text", idxName + (readOnly ? " (R)" : ""));
      iName = find(pOver, "iName");
      setString(iName, "text", pName + (readOnly ? " (Read-Only)" : ""));
      final Object dirImpl = find("dirImpl");
      String implName = "N/A";
      if (dir == null) {
        if (ir != null) {
          implName = "N/A (reader is " + ir.getClass().getName() + ")";
        }
      }
      else {
        implName = dir.getClass().getName();
      }
      setString(dirImpl, "text", implName);
      final Object fileSize = find("iFileSize");
      final long totalFileSize = Util.calcTotalFileSize(pName, dir);
      setString(fileSize, "text",
        Util.normalizeSize(totalFileSize) + Util.normalizeUnit(totalFileSize));
      final Object iFormat = find(pOver, "iFormat");
      final Object iCaps = find(pOver, "iCaps");
      String formatText = "N/A";
      String formatCaps = "N/A";
      if (dir != null) {
        final IndexGate.FormatDetails formatDetails = IndexGate.getIndexFormat(dir);
        formatText = formatDetails.genericName;
        formatCaps = formatDetails.capabilities;
      }
      setString(iFormat, "text", formatText);
      setString(iCaps, "text", formatCaps);
      if (ir == null) {
        return;
      }
      // we need IndexReader from now on
      idxInfo = new IndexInfo(ir, pName);
      Object iDocs = find(pOver, "iDocs");
      final String numdocs = String.valueOf(ir.numDocs());
      setString(iDocs, "text", numdocs);
      iDocs = find("iDocs1");
      setString(iDocs, "text", String.valueOf(ir.maxDoc() - 1));
      final Object iFields = find(pOver, "iFields");
      fn = idxInfo.getFieldNames();
      if (fn.size() == 0) {
        showStatus("Empty index.");
      }
      showFiles(dir, null);
      if (ir instanceof CompositeReader) {
        ar = new SlowCompositeReaderWrapper((CompositeReader) ir);
      }
      else if (ir instanceof AtomicReader) {
        ar = (AtomicReader) ir;
      }
      if (ar != null) {
        infos = ar.getFieldInfos();
      }
      showCommits();
      final Object fList = find(pOver, "fList");
      final Object defFld = find("defFld");
      final Object fCombo = find("fCombo");
      final TreeSet<String> fields = new TreeSet<String>(fn);
      idxFields = fields.toArray(new String[fields.size()]);
      setString(iFields, "text", String.valueOf(idxFields.length));
      final Object iTerms = find(pOver, "iTerms");
      if (!slowAccess) {
        final Thread t = new Thread() {

          @Override
          public void run() {
            final Object r = create("row");
            final Object cell = create("cell");
            add(r, cell);
            add(fList, r);
            setBoolean(cell, "enabled", false);
            setString(cell, "text", "..wait..");
            try {
              numTerms = idxInfo.getNumTerms();
              termCounts = idxInfo.getFieldTermCounts();
              setString(iTerms, "text", String.valueOf(numTerms));
              initFieldList(fList, fCombo, defFld);
            }
            catch (final Exception e) {
              e.printStackTrace();
              numTerms = -1;
              termCounts = Collections.emptyMap();
              showStatus("ERROR: can't count terms per field");
            }
          }
        };
        t.start();
      }
      else {
        setString(iTerms, "text", "N/A");
        initFieldList(fList, fCombo, defFld);
      }
      final Object iDel = find(pOver, "iDelOpt");
      final String sDel = ir.hasDeletions() ? "Yes (" + ir.numDeletedDocs() + ")" : "No";
      final IndexCommit ic =
        ir instanceof DirectoryReader ? ((DirectoryReader) ir).getIndexCommit() : null;
        final String opt = ic != null ? ic.getSegmentCount() == 1 ? "Yes" : "No" : "?";
        final String sDelOpt = sDel + " / " + opt;
        setString(iDel, "text", sDelOpt);
        final Object iVer = find(pOver, "iVer");
        String verText = "N/A";
        if (ic != null) {
          verText = Long.toHexString(((DirectoryReader) ir).getVersion());
        }
        setString(iVer, "text", verText);
        final Object iTiiDiv = find(pOver, "iTiiDiv");
        String divText = "N/A";
        if (ir instanceof DirectoryReader) {
          final List<AtomicReaderContext> contexts = ir.leaves();
          if (contexts.size() > 0 && contexts.get(0).reader() instanceof SegmentReader) {
            divText =
              String.valueOf(((SegmentReader) contexts.get(0).reader()).getTermInfosIndexDivisor());
          }
        }
        setString(iTiiDiv, "text", divText);
        final Object iCommit = find(pOver, "iCommit");
        String commitText = "N/A";
        if (ic != null) {
          commitText =
            ic.getSegmentsFileName() + " (generation=" + ic.getGeneration() + ", segs="
              + ic.getSegmentCount() + ")";
        }
        setString(iCommit, "text", commitText);
        final Object iUser = find(pOver, "iUser");
        String userData = null;
        if (ic != null) {
          final Map<String, String> userDataMap = ic.getUserData();
          if (userDataMap != null && !userDataMap.isEmpty()) {
            userData = userDataMap.toString();
          }
          else {
            userData = "--";
          }
        }
        else {
          userData = "(not supported)";
        }
        setString(iUser, "text", userData);
        final Object nTerms = find("nTerms");
        if (!slowAccess) {
          final Thread t = new Thread() {

            @Override
            public void run() {
              actionTopTerms(nTerms);
            }
          };
          t.start();
        }
    }
    catch (final Exception e) {
      e.printStackTrace();
      errorMsg(e.getMessage());
    }
  }

  private void initFieldList(final Object fList, final Object fCombo, final Object defFld) {
    removeAll(fList);
    removeAll(fCombo);
    removeAll(defFld);
    setString(fCombo, "text", idxFields[0]);
    setString(defFld, "text", idxFields[0]);
    final NumberFormat intCountFormat = NumberFormat.getIntegerInstance();
    final NumberFormat percentFormat = NumberFormat.getNumberInstance();
    intCountFormat.setGroupingUsed(true);
    percentFormat.setMaximumFractionDigits(2);
    // sort by names now
    for (final String s : idxFields) {
      final Object row = create("row");
      putProperty(row, "fName", s);
      add(fList, row);
      Object cell = create("cell");
      setString(cell, "text", s);
      add(row, cell);
      if (termCounts != null) {
        cell = create("cell");
        final FieldTermCount ftc = termCounts.get(s);
        if (ftc != null) {
          final long cnt = ftc.termCount;
          setString(cell, "text", intCountFormat.format(cnt));
          setChoice(cell, "alignment", "right");
          add(row, cell);
          final float pcent = (float) (cnt * 100) / (float) numTerms;
          cell = create("cell");
          setString(cell, "text", percentFormat.format(pcent) + " %");
          setChoice(cell, "alignment", "right");
          add(row, cell);
        }
        else {
          setString(cell, "text", "0");
          setChoice(cell, "alignment", "right");
          add(row, cell);
          cell = create("cell");
          setString(cell, "text", "0.00 %");
          setChoice(cell, "alignment", "right");
          add(row, cell);
        }
      }
      else {
        cell = create("cell");
        setString(cell, "text", "N/A");
        add(row, cell);
        cell = create("cell");
        add(row, cell);
      }
      cell = create("cell");
      setChoice(cell, "alignment", "right");
      Decoder dec = decoders.get(s);
      if (dec == null) {
        dec = defDecoder;
      }
      setString(cell, "text", dec.toString());
      add(row, cell);
      // populate combos
      Object choice = create("choice");
      add(fCombo, choice);
      setString(choice, "text", s);
      putProperty(choice, "fName", s);
      choice = create("choice");
      add(defFld, choice);
      setString(choice, "text", s);
      putProperty(choice, "fName", s);
    }
    setString(find("defFld"), "text", idxFields[0]);
    // Remove columns
    final Object header = get(find("sTable"), "header");
    removeAll(header);
    Object c = create("column");
    setString(c, "text", "#");
    setInteger(c, "width", 40);
    add(header, c);
    c = create("column");
    setString(c, "text", "Score");
    setInteger(c, "width", 50);
    add(header, c);
    c = create("column");
    setString(c, "text", "Doc. Id");
    setInteger(c, "width", 60);
    add(header, c);
    for (int j = 0; j < idxFields.length; j++) {
      c = create("column");
      setString(c, "text", idxFields[j]);
      add(header, c);
    }
  }

  private void showCommits() throws Exception {
    final Object commitsTable = find("commitsTable");
    removeAll(commitsTable);
    if (dir == null) {
      final Object row = create("row");
      final Object cell = create("cell");
      setString(cell, "text", "<not available>");
      setBoolean(cell, "enabled", false);
      add(row, cell);
      add(commitsTable, row);
      return;
    }
    final List<IndexCommit> commits = DirectoryReader.listCommits(dir);
    final IndexCommit current =
      ir instanceof DirectoryReader ? ((DirectoryReader) ir).getIndexCommit() : null;
      // commits are ordered from oldest to newest ?
      final Iterator<IndexCommit> it = commits.iterator();
      int rowNum = 0;
      while (it.hasNext()) {
        final IndexCommit commit = it.next();
        // figure out the name of the segment files
        final Collection<String> files = commit.getFileNames();
        final Iterator<String> itf = files.iterator();
        final Object row = create("row");
        final boolean enabled = rowNum < commits.size() - 1;
        final Color color = Color.BLUE;
        rowNum++;
        add(commitsTable, row);
        putProperty(row, "commit", commit);
        if (enabled) {
          putProperty(row, "commitDeletable", Boolean.TRUE);
        }
        Object cell = create("cell");
        final String gen = String.valueOf(commit.getGeneration());
        setString(cell, "text", gen);
        add(row, cell);
        cell = create("cell");
        setString(cell, "text", commit.isDeleted() ? "Y" : "N");
        add(row, cell);
        cell = create("cell");
        setString(cell, "text", String.valueOf(commit.getSegmentCount()));
        add(row, cell);
        cell = create("cell");
        final Map userData = commit.getUserData();
        if (userData != null && !userData.isEmpty()) {
          setString(cell, "text", userData.toString());
        }
        else {
          setString(cell, "text", "");
        }
        add(row, cell);
        if (commit.equals(current)) {
          final Object[] cells = getItems(row);
          for (final Object c : cells) {
            setColor(c, "foreground", color);
          }
        }
      }
  }

  public void showSegments(final Object commitsTable) throws Exception {
    final Object segTable = find("segmentsTable");
    removeAll(segTable);
    final Object[] rows = getSelectedItems(commitsTable);
    if (rows == null || rows.length == 0) {
      showStatus("No commit point selected.");
      return;
    }
    final Object row = rows[0];
    final IndexCommit commit = (IndexCommit) getProperty(row, "commit");
    if (commit == null) {
      showStatus("Can't retrieve commit point (application error)");
      return;
    }
    final Object segGen = find("segGen");
    setString(segGen, "text", commit.getSegmentsFileName() + " (gen " + commit.getGeneration()
      + ")");
    final String segName = commit.getSegmentsFileName();
    final SegmentInfos infos = new SegmentInfos();
    try {
      infos.read(dir, segName);
    }
    catch (final Exception e) {
      e.printStackTrace();
      errorMsg("Error reading segment infos for '" + segName + ": " + e.toString());
      return;
    }
    for (final SegmentInfoPerCommit si : infos.asList()) {
      final Object r = create("row");
      add(segTable, r);
      Object cell = create("cell");
      add(r, cell);
      setString(cell, "text", si.info.name);
      cell = create("cell");
      add(r, cell);
      setString(cell, "text", String.valueOf(si.getDelGen()));
      setChoice(cell, "alignment", "right");
      cell = create("cell");
      add(r, cell);
      setString(cell, "text", String.valueOf(si.getDelCount()));
      setChoice(cell, "alignment", "right");
      cell = create("cell");
      add(r, cell);
      setString(cell, "text", String.valueOf(si.info.getDocCount()));
      setChoice(cell, "alignment", "right");
      cell = create("cell");
      add(r, cell);
      setString(cell, "text", si.info.getVersion());
      cell = create("cell");
      add(r, cell);
      setString(cell, "text", si.info.getCodec().getName());
      final long size = si.sizeInBytes();
      cell = create("cell");
      add(r, cell);
      setString(cell, "text", Util.normalizeSize(size) + Util.normalizeUnit(size));
      setChoice(cell, "alignment", "right");
      cell = create("cell");
      add(r, cell);
      setString(cell, "text", si.info.getUseCompoundFile() ? "Y" : "N");

      putProperty(r, "si", si);
    }
    final Object diagsTable = find("diagsTable");
    removeAll(diagsTable);
  }

  public void showDiagnostics(final Object segmentsTable) {
    final Object diagsTable = find("diagsTable");
    removeAll(diagsTable);
    final Object row = getSelectedItem(segmentsTable);
    if (row == null) {
      return;
    }
    final SegmentInfoPerCommit si = (SegmentInfoPerCommit) getProperty(row, "si");
    if (si == null) {
      showStatus("Missing SegmentInfoPerCommit???");
      return;
    }
    Map<String, String> map = si.info.attributes();
    if (map != null) {
      for (final Entry<String, String> e : map.entrySet()) {
        final Object r = create("row");
        add(diagsTable, r);
        Object cell = create("cell");
        setString(cell, "text", "A");
        add(r, cell);
        cell = create("cell");
        setString(cell, "text", e.getKey());
        add(r, cell);
        cell = create("cell");
        setString(cell, "text", e.getValue());
        add(r, cell);
      }
    }
    // separator
    // Object r1 = create("row");
    // add(diagsTable, r1);
    // Object c1 = create("cell");
    // setBoolean(c1, "enabled", false);
    // add(r1, c1);
    map = si.info.getDiagnostics();
    if (map != null) {
      for (final Entry<String, String> e : map.entrySet()) {
        final Object r = create("row");
        add(diagsTable, r);
        Object cell = create("cell");
        setString(cell, "text", "D");
        add(r, cell);
        cell = create("cell");
        setString(cell, "text", e.getKey());
        add(r, cell);
        cell = create("cell");
        setString(cell, "text", e.getValue());
        add(r, cell);
      }
    }
    // separator
    Object r1 = create("row");
    add(diagsTable, r1);
    Object c1 = create("cell");
    setBoolean(c1, "enabled", false);
    add(r1, c1);
    // codec info
    final Codec codec = si.info.getCodec();
    map = new LinkedHashMap<String, String>();
    map.put("codecName", codec.getName());
    map.put("codecClassName", codec.getClass().getName());
    map.put("docValuesFormat", codec.docValuesFormat().getClass().getName());
    map.put("fieldInfosFormat", codec.fieldInfosFormat().getClass().getName());
    map.put("liveDocsFormat", codec.liveDocsFormat().getClass().getName());
    map.put("normsFormat", codec.normsFormat().getClass().getName());
    map.put("postingsFormat", codec.postingsFormat().toString() + " "
      + codec.postingsFormat().getClass().getName());
    map.put("segmentInfoFormat", codec.segmentInfoFormat().getClass().getName());
    map.put("storedFieldsFormat", codec.storedFieldsFormat().getClass().getName());
    map.put("termVectorsFormat", codec.termVectorsFormat().getClass().getName());
    try {
      final List<String> files = new ArrayList<String>(si.files());
      Collections.sort(files);
      map.put("---files---", files.toString());
      if (si.info.getUseCompoundFile()) {
        final Directory d =
          new CompoundFileDirectory(dir, IndexFileNames.segmentFileName(si.info.name, "",
            IndexFileNames.COMPOUND_FILE_EXTENSION), IOContext.READ, false);
        files.clear();
        files.addAll(Arrays.asList(d.listAll()));
        d.close();
        Collections.sort(files);
        map.put("-CFS-files-", files.toString());
      }
    }
    catch (final Exception e) {
      e.printStackTrace();
      map.put("---files---", "Exception: " + e.toString());
    }
    for (final Entry<String, String> e : map.entrySet()) {
      final Object r = create("row");
      add(diagsTable, r);
      Object cell = create("cell");
      setString(cell, "text", "C");
      add(r, cell);
      cell = create("cell");
      setString(cell, "text", e.getKey());
      add(r, cell);
      cell = create("cell");
      setString(cell, "text", e.getValue());
      add(r, cell);
    }
    // fieldInfos
    try {
      final SegmentReader sr = new SegmentReader(si, 1, IOContext.READ);
      final FieldInfos fis = sr.getFieldInfos();
      map = new LinkedHashMap<String, String>();
      final List<String> flds = new ArrayList<String>(fis.size());
      for (final FieldInfo fi : fis) {
        flds.add(fi.name);
      }
      Collections.sort(flds);
      map.put("L---fields---", flds.toString());
      for (final String fn : flds) {
        final FieldInfo fi = fis.fieldInfo(fn);
        map.put("A" + fi.name, fi.attributes().toString());
      }
      map.put("F---flags----", "IdfpoPVNtxxDtxx");
      for (final String fn : flds) {
        final FieldInfo fi = fis.fieldInfo(fn);
        map.put("F" + fi.name, Util.fieldFlags(null, fi));
      }
      sr.close();
      // separator
      r1 = create("row");
      add(diagsTable, r1);
      c1 = create("cell");
      setBoolean(c1, "enabled", false);
      add(r1, c1);
      for (final Entry<String, String> e : map.entrySet()) {
        final Object r = create("row");
        add(diagsTable, r);
        Object cell = create("cell");
        setString(cell, "text", "F" + e.getKey().charAt(0));
        add(r, cell);
        cell = create("cell");
        setString(cell, "text", e.getKey().substring(1));
        add(r, cell);
        cell = create("cell");
        setString(cell, "text", e.getValue());
        if (e.getKey().startsWith("F")) {
          setFont(cell, courier);
        }
        add(r, cell);
      }
    }
    catch (final IOException e1) {
      e1.printStackTrace();
    }
  }

  public void openCommit(final Object commitsTable) throws IOException {
    final Object row = getSelectedItem(commitsTable);
    if (row == null) {
      showStatus("No commit point selected.");
      return;
    }
    final IndexCommit commit = (IndexCommit) getProperty(row, "commit");
    if (commit == null) {
      showStatus("Can't retrieve commit point (application error)");
      return;
    }
    final IndexCommit current =
      ir instanceof DirectoryReader ? ((DirectoryReader) ir).getIndexCommit() : null;
      if (commit.equals(current)) {
        showStatus("Already open at this commit point.");
        return;
      }
      ir.close();
      IndexDeletionPolicy policy;
      if (keepCommits) {
        policy = new KeepAllIndexDeletionPolicy();
      }
      else {
        policy = new KeepLastIndexDeletionPolicy();
      }
      ir = DirectoryReader.open(commit);
      initOverview();
  }

  public void showCommitFiles(final Object commitTable) throws Exception {
    final List<IndexCommit> commits = new ArrayList<IndexCommit>();
    final Object[] rows = getSelectedItems(commitTable);
    if (rows == null || rows.length == 0) {
      showFiles(dir, commits);
      return;
    }
    for (int i = 0; i < rows.length; i++) {
      final IndexCommit commit = (IndexCommit) getProperty(rows[i], "commit");
      if (commit != null) {
        commits.add(commit);
      }
    }
    showFiles(dir, commits);
    showSegments(commitTable);
  }

  private void showFiles(final Directory dir, final List<IndexCommit> commits) throws Exception {
    final Object filesTable = find("filesTable");
    if (dir == null) {
      removeAll(filesTable);
      final Object row = create("row");
      final Object cell = create("cell");
      setString(cell, "text", "<not available>");
      setBoolean(cell, "enabled", false);
      add(row, cell);
      add(filesTable, row);
      return;
    }
    final Object iFileSize = find("iFileSize");
    long totalSize = 0;
    final String[] physFiles = dir.listAll();
    final Set<String> files = new HashSet<String>();
    if (commits != null && commits.size() > 0) {
      for (int i = 0; i < commits.size(); i++) {
        final IndexCommit commit = commits.get(i);
        files.addAll(commit.getFileNames());
      }
    }
    else {
      files.addAll(Arrays.asList(physFiles));
    }
    final List<String> uFiles = new ArrayList<String>(files);
    Collections.sort(uFiles);
    final List<String> segs = getIndexFileNames(dir);
    final List<String> dels = getIndexDeletableNames(dir);
    final Font courier2 = courier.deriveFont(courier.getSize() * 1.1f);
    removeAll(filesTable);
    for (int i = 0; i < uFiles.size(); i++) {
      final String fileName = uFiles.get(i);
      String pathName;
      if (pName.endsWith(File.separator)) {
        pathName = pName;
      }
      else {
        pathName = pName + File.separator;
      }
      final File file = new File(pathName + fileName);
      final boolean deletable = dels.contains(fileName.intern());
      final String inuse = getFileFunction(fileName);
      final Object row = create("row");
      final Object nameCell = create("cell");
      setString(nameCell, "text", fileName);
      setString(nameCell, "tooltip", inuse);
      add(row, nameCell);
      final Object sizeCell = create("cell");
      final long length = file.length();
      totalSize += length;
      setString(sizeCell, "text", Util.normalizeSize(length) + Util.normalizeUnit(length));
      setChoice(sizeCell, "alignment", "right");
      setFont(sizeCell, courier2);
      add(row, sizeCell);
      final Object delCell = create("cell");
      setString(delCell, "text", deletable ? "YES" : "-");
      add(row, delCell);
      final Object inuseCell = create("cell");
      setString(inuseCell, "text", inuse);
      add(row, inuseCell);
      add(filesTable, row);
    }
    setString(iFileSize, "text", Util.normalizeSize(totalSize) + Util.normalizeUnit(totalSize));
  }

  private String getFileFunction(final String file) {
    String res = IndexGate.getFileFunction(file);
    if (res == null) {
      res = "YES";
    }
    return res;
  }

  private void syncMRU(final Object path) {
    removeAll(path);
    for (final Iterator iter = Prefs.getMruList().iterator(); iter.hasNext();) {
      final String element = (String) iter.next();
      final Object choice = create("choice");
      setString(choice, "text", element);
      add(path, choice);
    }
  }

  /**
   * Update the list of top terms.
   *
   * @param nTerms Thinlet widget containing the number of top terms to show
   */
  public void actionTopTerms(final Object nTerms) {
    if (ir == null) {
      showStatus(MSG_NOINDEX);
      return;
    }
    final String sndoc = getString(nTerms, "text");
    int nd = 50;
    try {
      nd = Integer.parseInt(sndoc);
    }
    catch (final Exception e) {
    }
    final int ndoc = nd;
    final Object[] fields = getSelectedItems(find("fList"));
    String[] flds = null;
    if (fields == null || fields.length == 0) {
      flds = idxFields;
    }
    else {
      flds = new String[fields.length];
      for (int i = 0; i < fields.length; i++) {
        flds[i] = (String) getProperty(fields[i], "fName");
      }
    }
    final String[] fflds = flds;
    final SlowThread st = new SlowThread(this) {

      @Override
      public void execute() {
        try {
          final TermStats[] topTerms = HighFreqTerms.getHighFreqTerms(ir, ndoc, fflds);
          final Object table = find("tTable");
          removeAll(table);
          if (topTerms == null || topTerms.length == 0) {
            final Object row = create("row");
            Object cell = create("cell");
            add(row, cell);
            cell = create("cell");
            add(row, cell);
            cell = create("cell");
            add(row, cell);
            cell = create("cell");
            setBoolean(cell, "enabled", false);
            setString(cell, "text", "No Results");
            add(row, cell);
            add(table, row);
            return;
          }
          for (int i = 0; i < topTerms.length; i++) {
            final Object row = create("row");
            add(table, row);
            putProperty(row, "term", new Term(topTerms[i].field, topTerms[i].termtext));
            putProperty(row, "ti", topTerms[i]);
            Object cell = create("cell");
            setChoice(cell, "alignment", "right");
            setString(cell, "text", String.valueOf(i + 1));
            add(row, cell);
            cell = create("cell");
            setChoice(cell, "alignment", "right");
            setString(cell, "text", String.valueOf(topTerms[i].docFreq) + "  ");
            add(row, cell);
            cell = create("cell");
            setString(cell, "text", topTerms[i].field);
            add(row, cell);
            cell = create("cell");
            Decoder dec = decoders.get(topTerms[i].field);
            if (dec == null) {
              dec = defDecoder;
            }
            String s;
            try {
              s = dec.decodeTerm(topTerms[i].field, topTerms[i].termtext.utf8ToString());
            }
            catch (final Throwable e) {
              // e.printStackTrace();
              s = topTerms[i].termtext.utf8ToString();
              setColor(cell, "foreground", Color.RED);
            }
            setString(cell, "text", "  " + s);
            add(row, cell);
          }
        }
        catch (final Exception e) {
          e.printStackTrace();
          errorMsg(e.getMessage());
        }
      }
    };
    if (slowAccess) {
      st.start();
    }
    else {
      st.execute();
    }
  }

  public void clipTopTerms(final Object tTable) {
    final Object[] rows = getItems(tTable);
    final StringBuffer sb = new StringBuffer();
    for (int i = 0; i < rows.length; i++) {
      final TermStats ti = (TermStats) getProperty(rows[i], "ti");
      if (ti == null) {
        continue;
      }
      sb.append(ti.docFreq + "\t" + ti.field + "\t" + ti.termtext.utf8ToString() + "\n");
    }
    final StringSelection sel = new StringSelection(sb.toString());
    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel, this);
  }

  /**
   * Switch to a view that shows all documents containing selected term.
   *
   * @param tTable Thinlet table widget, where selected row contains a property named "term", which is the selected
   *          {@link Term} instance
   */
  public void browseTermDocs(final Object tTable) {
    final Object row = getSelectedItem(tTable);
    if (row == null) {
      return;
    }
    final Term t = (Term) getProperty(row, "term");
    if (t == null) {
      return;
    }
    final Object tabpane = find("maintpane");
    setInteger(tabpane, "selected", 1);
    _showTerm(find("fCombo"), find("fText"), t);
    showFirstTermDoc(find("fText"));
    repaint();

  }

  public void showTermDocs(final Object tTable) {
    final Object row = getSelectedItem(tTable);
    if (row == null) {
      return;
    }
    final Term t = (Term) getProperty(row, "term");
    if (t == null) {
      return;
    }
    final Object tabpane = find("maintpane");
    setInteger(tabpane, "selected", 2);
    final Object qField = find("qField");
    setString(qField, "text", t.field() + ":" + t.text());
    search(qField);
    repaint();

  }

  /**
   * Undelete all deleted documents in the current index. This method also updates the overview.
   */
  public void actionUndelete() {
    if (ir == null) {
      showStatus(MSG_NOINDEX);
      return;
    }
    if (readOnly) {
      showStatus(MSG_READONLY);
      return;
    }
    if (!(ir instanceof DirectoryReader)) {
      showStatus("Not possible with " + ir.getClass().getSimpleName());
      return;
    }
    errorMsg("Not supported in Lucene 4 API");
    /*
     * try { ir.undeleteAll(); initOverview(); } catch (Exception e) { e.printStackTrace(); errorMsg(e.getMessage()); }
     */
  }

  /** Not implemented yet... */
  public void actionConvert(final Object method) {

  }

  public List<String> getIndexDeletableNames(final Directory d) {
    if (d == null) {
      return null;
    }
    List<String> deletable = null;
    try {
      deletable = IndexGate.getDeletableFiles(d);
    }
    catch (final Exception e) {
      e.printStackTrace();
    }
    // for (int i = 0; i < deletable.size(); i++) {
    // System.out.println(" -del " + deletable.get(i));
    // }
    if (deletable == null) {
      deletable = Collections.EMPTY_LIST;
    }
    return deletable;
  }

  public Directory getDirectory() {
    return dir;
  }

  public IndexReader getIndexReader() {
    return ir;
  }

  public void setIndexReader(final IndexReader reader, final String indexName) {
    if (reader == null) {
      return;
    }
    try {
      if (is != null) {
        is = null;
      }
      if (ir != null) {
        ir.close();
        ir = null;
      }
      if (ar != null) {
        ar.close();
        ar = null;
      }
      if (dir != null) {
        dir.close();
        dir = null;
      }
      if (reader instanceof DirectoryReader) {
        dir = ((DirectoryReader) reader).directory();
      }
      else {
        dir = null;
      }
      ir = reader;
      is = new IndexSearcher(ir);
      pName = indexName;
      initOverview();
      initPlugins();
    }
    catch (final Exception e) {
      e.printStackTrace();
      errorMsg("Setting new IndexReader failed: " + e.toString());
    }
  }

  public List<String> getIndexFileNames(final Directory d) {
    if (d == null) {
      return null;
    }
    List<String> names = null;
    try {
      names = IndexGate.getIndexFiles(d);
    }
    catch (final Exception e) {
      e.printStackTrace();
    }
    // for (int i = 0; i < names.size(); i++) {
    // System.out.println(" -seg " + names.get(i));
    // }
    return names;
  }

  public void actionCheckIndex() {
    if (dir == null) {
      errorMsg("No directory - open index directory first (you may use the 'no IndexReader' option).");
      return;
    }
    final Object dialog = addComponent(null, "/xml/checkindex.xml", null, null);
    final Object dirName = find(dialog, "dirName");
    setString(dirName, "text", pName);
    add(dialog);
  }

  public void checkIndex(final Object dialog) {
    final Thread t = new Thread() {

      @Override
      public void run() {
        final Object panel = find(dialog, "msg");
        final Object fixPanel = find(dialog, "fixPanel");
        final PanelPrintWriter ppw = new PanelPrintWriter(Leia.this, panel);
        final Object ckRes = find(dialog, "ckRes");
        CheckIndex.Status status = null;
        final CheckIndex ci = new CheckIndex(dir);
        ci.setInfoStream(ppw);
        putProperty(dialog, "checkIndex", ci);
        putProperty(dialog, "ppw", ppw);
        try {
          status = ci.checkIndex();
        }
        catch (final Exception e) {
          ppw.println("ERROR: caught exception, giving up.\n\n");
          e.printStackTrace();
          e.printStackTrace(ppw);
        }
        if (status != null) {
          Leia.this.putProperty(dialog, "checkStatus", status);
          String statMsg;
          if (status.clean) {
            statMsg = "OK";
          }
          else if (status.toolOutOfDate) {
            statMsg = "ERROR: Can't check - tool out-of-date";
          }
          else {
            // show fixPanel
            setBoolean(fixPanel, "visible", true);
            repaint(dialog);
            statMsg = "BAD: ";
            if (status.cantOpenSegments) {
              statMsg += "cantOpenSegments ";
            }
            if (status.missingSegments) {
              statMsg += "missingSegments ";
            }
            if (status.missingSegmentVersion) {
              statMsg += "missingSegVersion ";
            }
            if (status.numBadSegments > 0) {
              statMsg += "numBadSegments=" + status.numBadSegments + " ";
            }
            if (status.totLoseDocCount > 0) {
              statMsg += "lostDocCount=" + status.totLoseDocCount + " ";
            }
          }
          setString(ckRes, "text", statMsg);
        }
      }
    };
    t.start();
  }

  public void fixIndex(final Object dialog) {
    final Thread t = new Thread() {

      @Override
      public void run() {
        final CheckIndex ci = (CheckIndex) getProperty(dialog, "checkIndex");
        if (ci == null) {
          errorMsg("You need to run 'Check Index' first.");
          return;
        }
        final CheckIndex.Status status = (CheckIndex.Status) getProperty(dialog, "checkStatus");
        if (status == null) {
          errorMsg("You need to run 'Check Index' first.");
          return;
        }
        // use codec from the first valid segment
        Codec c = null;
        for (final SegmentInfoStatus ssi : status.segmentInfos) {
          if (ssi.codec != null) {
            c = ssi.codec;
            break;
          }
        }
        if (c == null) {
          showStatus("Can't determine Codec, using default");
          c = Codec.getDefault();
        }
        final Object fixRes = find(dialog, "fixRes");
        final PanelPrintWriter ppw = (PanelPrintWriter) getProperty(dialog, "ppw");
        try {
          ci.fixIndex(status, c);
          setString(fixRes, "text", "DONE. Review the output above.");
        }
        catch (final Exception e) {
          ppw.println("\nERROR during Fix Index:");
          e.printStackTrace(ppw);
          setString(fixRes, "text", "FAILED. Review the output above.");
        }
      }
    };
    t.start();
  }

  public boolean isFSBased(final Directory dir) {
    if (dir == null) {
      return false;
    }
    if (dir instanceof MMapDirectory || dir instanceof NIOFSDirectory || dir instanceof FSDirectory) {
      return true;
    }
    return false;
  }

  /**
   * This method will cleanup the current Directory of any content that is not the part of the index.
   */
  public void actionCleanup() {
    if (dir == null) {
      showStatus(MSG_NOINDEX);
      return;
    }
    try {
      List allFiles;
      boolean phys = false;
      if (isFSBased(dir)) {
        final File phyDir = new File(pName);
        phys = true;
        allFiles = new ArrayList(Arrays.asList(phyDir.list()));
      }
      else {
        allFiles = new ArrayList(Arrays.asList(dir.listAll()));
      }
      final List indexFiles = getIndexFileNames(dir);
      allFiles.removeAll(indexFiles);
      if (allFiles.size() == 0) {
        infoMsg("There are no files to be cleaned - target directory contains only index-related files.");
        return;
      }
      final Object dialog = addComponent(null, "/xml/cleanup.xml", null, null);
      final Object filesTable = find(dialog, "filesTable");
      for (int i = 0; i < allFiles.size(); i++) {
        final String fileName = (String) allFiles.get(i);
        final Object row = create("row");
        add(filesTable, row);
        putProperty(row, "fileName", fileName);
        final long size = dir.fileLength(fileName);
        Object cell;
        cell = create("cell");
        setString(cell, "text", Util.normalizeSize(size));
        add(row, cell);
        cell = create("cell");
        setString(cell, "text", Util.normalizeUnit(size));
        add(row, cell);
        cell = create("cell");
        setString(cell, "text", fileName);
        add(row, cell);
      }
      add(dialog);
    }
    catch (final Exception e) {
      errorMsg("Error: " + e.toString());
    }
  }

  public void toggleKeep(final Object filesTable) {
    final Object[] rows = getSelectedItems(filesTable);
    if (rows == null || rows.length == 0) {
      return;
    }
    for (int i = 0; i < rows.length; i++) {
      final Boolean Deletable = (Boolean) getProperty(rows[i], "deletable");
      boolean deletable = Deletable != null ? Deletable.booleanValue() : true;
      deletable = !deletable;
      putProperty(rows[i], "deletable", Boolean.valueOf(deletable));
      final Object[] cells = getItems(rows[i]);
      for (int k = 0; k < cells.length; k++) {
        if (!deletable) {
          setBoolean(cells[k], "enabled", false);
        }
        else {
          setBoolean(cells[k], "enabled", true);
        }
      }
    }
    repaint(filesTable);
  }

  public void _actionCleanup(final Object filesTable) {
    try {
      if (is != null) {
        is = null;
      }
      if (ir != null) {
        ir.close();
      }
      if (ar != null) {
        ar.close();
      }
    }
    catch (final Exception e) {
      e.printStackTrace();
    }
    final ArrayList noDel = new ArrayList();
    String errMsg = null;
    final boolean phys = isFSBased(dir);
    int deleted = 0;
    try {
      final Object[] rows = getItems(filesTable);
      for (int i = 0; i < rows.length; i++) {
        final String name = (String) getProperty(rows[i], "fileName");
        final Boolean Deletable = (Boolean) getProperty(rows[i], "deletable");
        final boolean deletable = Deletable != null ? Deletable.booleanValue() : true;
        if (!deletable) {
          continue;
        }
        if (phys) {
          final File f = new File(pName, name);
          if (!removeFile(f)) {
            noDel.add(name);
          }
          deleted++;
          continue;
        }
        else {
          dir.deleteFile(name);
          deleted++;
        }
      }
      if (noDel.size() > 0) {
        final StringBuffer msg = new StringBuffer("Some files could not be deleted:");
        for (int i = 0; i < noDel.size(); i++) {
          msg.append("\n" + noDel.get(i));
        }
        errMsg = msg.toString();
      }
    }
    catch (final Exception e) {
      e.printStackTrace();
      errMsg = "FAILED: " + e.getMessage();
    }
    finally {
      try {
        actionReopen();
      }
      catch (final Exception e) {
        e.printStackTrace();
        errMsg = "FAILED: " + e.getMessage();
      }
    }
    if (errMsg != null) {
      errorMsg(errMsg);
    }
    else if (deleted == 0) {
      infoMsg("No files were deleted.");
    }

  }

  /**
   * Recursively remove files and directories including the indicated root file.
   *
   * @param f root file name
   * @return true if successful, false otherwise. Note that if the result is false the tree may have been partially
   *         removed.
   */
  public boolean removeFile(final File f) {
    System.out.println("remove " + f);
    if (!f.isDirectory()) {
      return f.delete();
    }
    final File[] files = f.listFiles();
    boolean res = true;
    if (files != null) {
      for (int i = 0; i < files.length; i++) {
        res = res && removeFile(files[i]);
      }
    }
    res = res && f.delete();
    return res;
  }

  public void actionExport() {
    if (ir == null) {
      showStatus(MSG_NOINDEX);
      return;
    }
    final Object dialog = addComponent(this, "/xml/export.xml", null, null);
  }

  public void export(final Object dialog) {
    final Object ckOver = find(dialog, "ckOver");
    final Object ckGzip = find(dialog, "ckGzip");
    final Object path = find(dialog, "path");
    final Object ckRanges = find(dialog, "ckRanges");
    final Object list = find(dialog, "ranges");
    String fileName = getString(path, "text");
    fileName = fileName.trim();
    if (fileName.length() == 0) {
      errorMsg("No output file set.");
      return;
    }
    final boolean over = getBoolean(ckOver, "selected");
    final boolean gzip = getBoolean(ckGzip, "selected");
    if (gzip && !fileName.endsWith(".gz")) {
      fileName = fileName + ".gz";
      setString(path, "text", fileName);
    }
    final File out = new File(fileName);
    if (out.exists()) {
      if (out.isDirectory()) {
        errorMsg("Output already exists and is a directory.");
        return;
      }
      if (!over) {
        errorMsg("Output already exists.");
        return;
      }
    }
    Ranges ranges = null;
    if (getBoolean(ckRanges, "selected")) {
      final String rs = getString(list, "text");
      if (rs.trim().length() > 0) {
        try {
          ranges = Ranges.parse(rs);
        }
        catch (final Exception e) {
          errorMsg(e.toString());
          return;
        }
      }
    }
    final Object progressBar = find(dialog, "bar");
    final Object counter = find(dialog, "counter");
    final Object msg = find(dialog, "msg");
    final Observer obs = new Observer() {

      @Override
      public void update(final Observable o, final Object arg) {
        final ProgressNotification pn = (ProgressNotification) arg;
        setInteger(progressBar, "minimum", pn.minValue);
        setInteger(progressBar, "maximum", pn.maxValue);
        setInteger(progressBar, "value", pn.curValue);
        setString(msg, "text", pn.message);
        if (!pn.aborted) {
          setString(counter, "text", pn.curValue + " of " + pn.maxValue + " done.");
        }
        else {
          setString(counter, "text", "ABORTED at " + pn.curValue + " of " + pn.maxValue);
        }
      }
    };
    // toggle buttons
    setBoolean(find(dialog, "startButton"), "visible", false);
    setBoolean(find(dialog, "abortButton"), "visible", true);
    setBoolean(find(dialog, "closeButton"), "enabled", false);
    try {
      _runExport(out, gzip, obs, dialog, ranges);
    }
    catch (final IOException ioe) {
      errorMsg("Export failed: " + ioe.toString());
    }
  }

  XMLExporter exporter = null;

  public void _runExport(final File out, final boolean gzip, final Observer obs,
    final Object dialog, final Ranges ranges) throws IOException {
    exporter = new XMLExporter(ir, pName, decoders);
    exporter.addObserver(obs);
    final Thread t = new Thread() {

      @Override
      public void run() {
        OutputStream os = null;
        try {
          os = new FileOutputStream(out);
          if (gzip) {
            os = new GZIPOutputStream(os);
          }
          exporter.export(os, true, true, true, "index", ranges);
          exporter = null;
        }
        catch (final Exception e) {
          e.printStackTrace();
          errorMsg("ERROR occurred, file may be incomplete: " + e.toString());
        }
        finally {
          setBoolean(find(dialog, "startButton"), "visible", true);
          setBoolean(find(dialog, "abortButton"), "visible", false);
          setBoolean(find(dialog, "closeButton"), "enabled", true);
          if (os != null) {
            try {
              os.flush();
              os.close();
            }
            catch (final Exception e) {
              errorMsg("ERROR closing output, file may be incomplete: " + e.toString());
            }
          }
        }
      }
    };
    t.start();
  }

  public void abortExport(final Object dialog) {
    if (exporter != null && exporter.isRunning()) {
      exporter.abort();
    }
  }

  /**
   * Optimize the current index
   *
   * @param method Thinlet menuitem widget containing the choice of index format. If the widget name is "optCompound"
   *          then the index will be optimized into compound format; otherwise a plain multi-file format will be used.
   *          <p>
   *          NOTE: this method is usually invoked from the GUI, and it also re-initializes GUI and plugins.
   *          </p>
   */
  public void actionOptimize() {
    if (dir == null) {
      showStatus(MSG_NOINDEX);
      return;
    }
    if (readOnly) {
      showStatus(MSG_READONLY);
      return;
    }
    final Object dialog = addComponent(null, "/xml/optimize.xml", null, null);
    setString(find(dialog, "dirName"), "text", pName);
    add(dialog);
  }

  /**
   * Optimize the index.
   */
  public void optimize(final Object dialog) {
    final Thread t = new Thread() {

      @Override
      public void run() {
        IndexWriter iw = null;
        final Object optimizeButton = find(dialog, "optimizeButton");
        setBoolean(optimizeButton, "enabled", false);
        final Object closeButton = find(dialog, "closeButton");
        setBoolean(closeButton, "enabled", false);
        final Object msg = find(dialog, "msg");
        final Object stat = find(dialog, "stat");
        setString(stat, "text", "Running ...");
        final PanelPrintWriter ppw = new PanelPrintWriter(Leia.this, msg);
        final boolean useCompound = getBoolean(find(dialog, "optCompound"), "selected");
        final boolean expunge = getBoolean(find(dialog, "optExpunge"), "selected");
        final boolean keep = getBoolean(find(dialog, "optKeepAll"), "selected");
        final boolean useLast = getBoolean(find(dialog, "optLastCommit"), "selected");
        final Object tiiSpin = find(dialog, "tii");
        final Object segnumSpin = find(dialog, "segnum");
        final int tii = Integer.parseInt(getString(tiiSpin, "text"));
        final int segnum = Integer.parseInt(getString(segnumSpin, "text"));
        try {
          if (is != null) {
            is = null;
          }
          if (ir != null) {
            ir.close();
          }
          if (ar != null) {
            ar.close();
          }
          IndexDeletionPolicy policy;
          if (keep) {
            policy = new KeepAllIndexDeletionPolicy();
          }
          else {
            policy = new KeepLastIndexDeletionPolicy();
          }
          final IndexWriterConfig cfg = new IndexWriterConfig(LV, new WhitespaceAnalyzer(LV));
          if (!useLast) {
            final IndexCommit ic = ((DirectoryReader) ir).getIndexCommit();
            if (ic != null) {
              cfg.setIndexCommit(ic);
            }
          }
          cfg.setIndexDeletionPolicy(policy);
          cfg.setTermIndexInterval(tii);
          final MergePolicy p = cfg.getMergePolicy();
          if (p instanceof LogMergePolicy) {
            ((LogMergePolicy) p).setUseCompoundFile(useCompound);
            if (useCompound) {
              ((LogMergePolicy) p).setNoCFSRatio(1.0);
            }
          }
          else if (p instanceof TieredMergePolicy) {
            ((TieredMergePolicy) p).setUseCompoundFile(useCompound);
            if (useCompound) {
              ((TieredMergePolicy) p).setNoCFSRatio(1.0);
            }
          }
          cfg.setInfoStream(ppw);
          iw = new IndexWriter(dir, cfg);
          final long startSize = Util.calcTotalFileSize(pName, dir);
          final long startTime = System.currentTimeMillis();
          if (expunge) {
            iw.forceMergeDeletes();
          }
          else {
            if (segnum > 1) {
              iw.forceMerge(segnum, true);
            }
            else {
              iw.forceMerge(1, true);
            }
          }
          iw.commit();
          final long endTime = System.currentTimeMillis();
          final long endSize = Util.calcTotalFileSize(pName, dir);
          final long deltaSize = startSize - endSize;
          final String sign = deltaSize < 0 ? " Increased " : " Reduced ";
          final String sizeMsg =
            sign + Util.normalizeSize(Math.abs(deltaSize))
            + Util.normalizeUnit(Math.abs(deltaSize));
          final String timeMsg = String.valueOf(endTime - startTime) + " ms";
          showStatus(sizeMsg + " in " + timeMsg);
          iw.close();
          setString(stat, "text", "Finished OK.");
        }
        catch (final Exception e) {
          e.printStackTrace(ppw);
          setString(stat, "text", "ERROR - aborted.");
          errorMsg("ERROR optimizing: " + e.toString());
          if (iw != null) {
            try {
              iw.close();
            }
            catch (final Exception e1) {
            }
          }
        }
        finally {
          setBoolean(closeButton, "enabled", true);
        }
        try {
          actionReopen();
          is = new IndexSearcher(ir);
          // add dialog again
          add(dialog);
        }
        catch (final Exception e) {
          e.printStackTrace(ppw);
          errorMsg("ERROR reopening after optimize:\n" + e.getMessage());
        }
      }
    };
    t.start();
  }

  public void showPrevDoc(final Object docNum) {
    _showDoc(docNum, -1);
  }

  public void showNextDoc(final Object docNum) {
    _showDoc(docNum, +1);
  }

  public void showDoc(final Object docNum) {
    _showDoc(docNum, 0);
  }

  Document doc = null;
  int iNum;

  private void _showDoc(final Object docNum, final int incr) {
    if (ir == null) {
      showStatus(MSG_NOINDEX);
      return;
    }
    String num = getString(docNum, "text");
    if (num.trim().equals("")) {
      num = String.valueOf(-incr);
    }
    try {
      iNum = Integer.parseInt(num);
      iNum += incr;
      if (iNum < 0 || iNum >= ir.maxDoc()) {
        showStatus("Document number outside valid range.");
        return;
      }
      setString(docNum, "text", String.valueOf(iNum));
      final org.apache.lucene.util.Bits live = ar.getLiveDocs();
      if (live == null || live.get(iNum)) {
        final SlowThread st = new SlowThread(this) {

          @Override
          public void execute() {
            try {
              doc = ir.document(iNum);
              _showDocFields(iNum, doc);
            }
            catch (final Exception e) {
              e.printStackTrace();
              showStatus(e.getMessage());
            }
          }
        };
        if (slowAccess) {
          st.start();
        }
        else {
          st.execute();
        }
      }
      else {
        showStatus("Deleted document - not available.");
        _showDocFields(iNum, null);
      }
    }
    catch (final Exception e) {
      e.printStackTrace();
      showStatus(e.getMessage());
    }
  }

  public void actionAddDocument(final Object docTable) {
    if (ir == null) {
      errorMsg(MSG_NOINDEX);
      return;
    }
    final Object dialog = addComponent(null, "/xml/editdoc.xml", null, null);
    remove(find(dialog, "bDelAdd"));
    final Object cbAnalyzers = find(dialog, "cbAnalyzers");
    populateAnalyzers(cbAnalyzers);
    setInteger(cbAnalyzers, "selected", 0);
    add(dialog);
  }

  public void actionReconstruct(final Object docNumText) {
    final int[] nums = new int[1];
    try {
      final String numString = getString(docNumText, "text");
      nums[0] = Integer.parseInt(numString);
    }
    catch (final Exception e) {
      showStatus("ERROR: no valid document selected");
      return;
    }
    final Progress progress = new Progress(this);
    progress.setMessage("Reconstructing ...");
    progress.show();
    final Thread thr = new Thread() {

      @Override
      public void run() {
        try {
          final int docNum = nums[0];
          final DocReconstructor recon = new DocReconstructor(ir, idxFields, numTerms);
          recon.addObserver(progress);
          final Reconstructed doc = recon.reconstruct(docNum);
          final Object dialog = addComponent(null, "/xml/editdoc.xml", null, null);
          putProperty(dialog, "docNum", new Integer(docNum));
          final Object cbAnalyzers = find(dialog, "cbAnalyzers");
          populateAnalyzers(cbAnalyzers);
          setInteger(cbAnalyzers, "selected", 0);
          final Object editTabs = find(dialog, "editTabs");
          setString(find(dialog, "docNum"), "text", "Fields of Doc #: " + docNum);
          for (int p = 0; p < idxFields.length; p++) {
            final String key = idxFields[p];
            if (!doc.hasField(key)) {
              continue;
            }
            final IndexableField[] fields = doc.getStoredFields().get(key);
            GrowableStringArray recField = doc.getReconstructedFields().get(key);
            int count = 0;
            if (recField != null) {
              count = 1;
            }
            if (fields != null && fields.length > count) {
              count = fields.length;
            }
            for (int i = 0; i < count; i++) {
              if (i > 0) {
                recField = null; // show it only for the first field
              }
              final Object tab = create("tab");
              setString(tab, "text", key);
              setFont(tab, getFont().deriveFont(Font.BOLD));
              add(editTabs, tab);
              final Object editfield = addComponent(tab, "/xml/editfield.xml", null, null);
              final Object fType = find(editfield, "fType");
              final Object sText = find(editfield, "sText");
              final Object rText = find(editfield, "rText");
              final Object fBoost = find(editfield, "fBoost");
              final Object cbStored = find(editfield, "cbStored");
              // Object cbCmp = find(editfield, "cbCmp");
              final Object cbBin = find(editfield, "cbBin");
              final Object cbIndexed = find(editfield, "cbIndexed");
              final Object cbTokenized = find(editfield, "cbTokenized");
              final Object cbTVF = find(editfield, "cbTVF");
              final Object cbTVFp = find(editfield, "cbTVFp");
              final Object cbTVFo = find(editfield, "cbTVFo");
              final Object cbONorms = find(editfield, "cbONorms");
              final Object cbOTF = find(editfield, "cbOTF");
              final Object stored = find(editfield, "stored");
              final Object restored = find(editfield, "restored");
              if (ar != null) {
                setBoolean(cbONorms, "selected", !ar.hasNorms(key));
              }
              Field f = null;
              if (fields != null && fields.length > i) {
                f = (Field) fields[i];
                setString(fType, "text", "Original stored field content");
                String text;
                if (f.binaryValue() != null) {
                  text = Util.bytesToHex(f.binaryValue(), true);
                  setBoolean(cbBin, "selected", true);
                }
                else {
                  text = f.stringValue();
                }
                setString(sText, "text", text);
                setString(fBoost, "text", String.valueOf(f.boost()));
                final IndexableFieldType t = f.fieldType();
                setBoolean(cbStored, "selected", t.stored());
                // Lucene 3.0 doesn't support compressed fields
                // setBoolean(cbCmp, "selected", false);
                setBoolean(cbIndexed, "selected", t.indexed());
                setBoolean(cbTokenized, "selected", t.tokenized());
                setBoolean(cbTVF, "selected", t.storeTermVectors());
                setBoolean(cbTVFp, "selected", t.storeTermVectorPositions());
                setBoolean(cbTVFo, "selected", t.storeTermVectorOffsets());
                // XXX omitTF needs fixing!
                // setBoolean(cbOTF, "selected", f.getOmitTermFreqAndPositions());
              }
              else {
                remove(stored);
              }
              if (recField != null) {
                String sep = " ";
                if (f == null) {
                  setString(fType, "text", "RESTORED content ONLY - check for errors!");
                  setColor(fType, "foreground", Color.red);
                }
                else {
                  setBoolean(rText, "editable", false);
                  setBoolean(rText, "border", false);
                  setString(restored, "text", "Tokenized (from all '" + key + "' fields)");
                  sep = ", ";
                }
                setBoolean(cbIndexed, "selected", true);
                setString(fBoost, "text", String.valueOf(1.0f));
                setString(rText, "text", recField.toString(sep));
              }
              else {
                remove(restored);
              }
            }
          }
          add(dialog);
          getPreferredSize(editTabs);
        }
        catch (final Exception e) {
          e.printStackTrace();
          showStatus(e.getMessage());
        }
        progress.hide();
      }
    };
    thr.start();
  }

  public boolean actionEditAdd(final Object editdoc) {
    final Document doc = new Document();
    final Object cbAnalyzers = find(editdoc, "cbAnalyzers");
    // reuse the logic in createAnalyzer - needs a prepared srchOptTabs
    final Object srchTabs = find("srchOptTabs");
    final Object cbType = find(srchTabs, "cbType");
    final String clazz = getString(cbAnalyzers, "text");
    setString(cbType, "text", clazz);
    final Analyzer a = createAnalyzer(srchTabs);
    if (a == null) {
      return false;
    }
    final Object editTabs = find(editdoc, "editTabs");
    final Object[] tabs = getItems(editTabs);
    for (int i = 0; i < tabs.length; i++) {
      final String name = getString(tabs[i], "text");
      if (name.trim().equals("")) {
        continue;
      }
      final Object fBoost = find(tabs[i], "fBoost");
      Object fText = find(tabs[i], "sText");
      if (fText == null) {
        fText = find(tabs[i], "rText");
      }
      final Object cbStored = find(tabs[i], "cbStored");
      final Object cbIndexed = find(tabs[i], "cbIndexed");
      final Object cbTokenized = find(tabs[i], "cbTokenized");
      final Object cbTVF = find(tabs[i], "cbTVF");
      final Object cbTVFo = find(tabs[i], "cbTVFo");
      final Object cbTVFp = find(tabs[i], "cbTVFp");
      final Object cbCmp = find(tabs[i], "cbCmp");
      final Object cbBin = find(tabs[i], "cbBin");
      final Object cbONorms = find(tabs[i], "cbONorms");
      final Object cbOTF = find(tabs[i], "cbOTF");
      final String text = getString(fText, "text");
      byte[] binValue;
      final boolean binary = getBoolean(cbBin, "selected");
      final FieldType ft = new FieldType();
      if (getBoolean(cbTVF, "selected")) {
        ft.setStoreTermVectors(true);
        if (getBoolean(cbTVFo, "selected")) {
          ft.setStoreTermVectorOffsets(true);
        }
        if (getBoolean(cbTVFp, "selected")) {
          ft.setStoreTermVectorPositions(true);
        }
      }
      else {
        ft.setStoreTermVectors(false);
      }
      // XXX omitTF needs fixing
      // f.setOmitTermFreqAndPositions(getBoolean(cbOTF, "selected"));
      if (getBoolean(cbOTF, "selected")) {
        ft.setIndexOptions(IndexOptions.DOCS_ONLY);
      }
      else {
        ft.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
      }
      ft.setStored(getBoolean(cbStored, "selected"));
      ft.setIndexed(getBoolean(cbIndexed, "selected"));
      ft.setTokenized(getBoolean(cbTokenized, "selected"));
      if (ft.stored() == false && ft.indexed() == false) {
        errorMsg("Field '" + name + "' is neither stored nor indexed.");
        return false;
      }
      ft.setOmitNorms(getBoolean(cbONorms, "selected"));
      Field f;
      if (binary) {
        try {
          binValue = Util.hexToBytes(text);
        }
        catch (final Throwable e) {
          errorMsg("Field '" + name + "': " + e.getMessage());
          return false;
        }
        f = new Field(name, binValue, 0, binValue.length, ft);
      }
      else {
        f = new Field(name, text, ft);
      }
      final String boostS = getString(fBoost, "text").trim();
      if (!boostS.equals("") && !boostS.equals("1.0")) {
        float boost = 1.0f;
        try {
          boost = Float.parseFloat(boostS);
        }
        catch (final Exception e) {
          e.printStackTrace();
        }
        f.setBoost(boost);
      }
      doc.add(f);
    }
    IndexWriter writer = null;
    boolean res = false;
    String msg = null;
    try {
      ir.close();
      if (ar != null) {
        ar.close();
      }
      writer = createIndexWriter();
      writer.addDocument(doc);
      res = true;
    }
    catch (final Exception e) {
      e.printStackTrace();
      msg = "FAILED: " + e.getMessage();
      res = false;
    }
    finally {
      try {
        if (writer != null) {
          writer.close();
        }
      }
      catch (final Exception e) {
        e.printStackTrace();
        res = false;
        msg = "FAILED: " + e.getMessage();
      }
      remove(editdoc);
      try {
        actionReopen();
        // show Documents tab
        final Object tabpane = find("maintpane");
        setInteger(tabpane, "selected", 1);
      }
      catch (final Exception e) {
        e.printStackTrace();
        res = false;
        msg = "FAILED: " + e.getMessage();
      }
    }
    if (!res) {
      errorMsg(msg);
    }
    return res;
  }

  public void actionEditAddField(final Object editdoc) {
    String name = getString(find(editdoc, "fNewName"), "text");
    if (name.trim().equals("")) {
      showStatus("FAILED: Field name is required.");
      return;
    }
    name = name.trim();
    final Object editTabs = find(editdoc, "editTabs");
    final Object tab = create("tab");
    setString(tab, "text", name);
    setFont(tab, getFont().deriveFont(Font.BOLD));
    add(editTabs, tab);
    addComponent(tab, "/xml/editfield.xml", null, null);
    repaint(editTabs);
  }

  public void actionEditDeleteField(final Object editfield) {
    final Object tab = getParent(editfield);
    remove(tab);
  }

  /** More Like this query from the current doc (or selected fields) */
  public void actionMLT(final Object docNum, final Object docTable) {
    if (ir == null) {
      errorMsg(MSG_NOINDEX);
      return;
    }
    int id = 0;
    try {
      id = Integer.parseInt(getString(docNum, "text"));
    }
    catch (final NumberFormatException nfe) {
      errorMsg("Invalid document number");
      return;
    }
    final MoreLikeThis mlt = new MoreLikeThis(ir);
    try {
      mlt.setFieldNames(Util.fieldNames(ir, true).toArray(new String[0]));
    }
    catch (final Exception e) {
      errorMsg("Exception collecting field names: " + e.toString());
      return;
    }
    mlt.setMinTermFreq(1);
    mlt.setMaxQueryTerms(50);
    final Analyzer a = createAnalyzer(find("srchOptTabs"));
    if (a == null) {
      return;
    }
    mlt.setAnalyzer(a);
    final Object[] rows = getSelectedItems(docTable);
    BooleanQuery similar = null;
    if (rows != null && rows.length > 0) {
      // collect text from fields
      final StringBuilder sb = new StringBuilder();
      for (int i = 0; i < rows.length; i++) {
        final Field f = (Field) getProperty(rows[i], "field");
        if (f == null) {
          continue;
        }
        final String s = f.stringValue();
        if (s == null || s.trim().length() == 0) {
          continue;
        }
        if (sb.length() > 0) {
          sb.append(" ");
        }
        sb.append(s);
      }
      try {
        similar = (BooleanQuery) mlt.like(new StringReader(sb.toString()), "field");
      }
      catch (final Exception e) {
        e.printStackTrace();
        errorMsg("FAILED: " + e.getMessage());
        return;
      }
    }
    else {
      try {
        similar = (BooleanQuery) mlt.like(id);
      }
      catch (final Exception e) {
        e.printStackTrace();
        errorMsg("FAILED: " + e.getMessage());
        return;
      }
    }
    if (similar.clauses() != null && similar.clauses().size() > 0) {
      // System.err.println("SIMILAR: " + similar);
      final Object tabpane = find("maintpane");
      setInteger(tabpane, "selected", 2);
      final Object qField = find("qField");
      setString(qField, "text", similar.toString());
    }
    else {
      showStatus("WARN: empty query - check Analyzer settings");
    }
  }

  private void _showDocFields(final int docid, final Document doc) {
    final Object table = find("docTable");
    final Object srchOpts = find("srchOptTabs");
    Similarity sim = createSimilarity(srchOpts);
    if (sim == null || !(sim instanceof TFIDFSimilarity)) {
      sim = defaultSimilarity;
    }
    setString(find("docNum"), "text", String.valueOf(docid));
    removeAll(table);
    putProperty(table, "doc", doc);
    putProperty(table, "docNum", new Integer(docid));
    if (doc == null) {
      setString(find("docNum1"), "text", String.valueOf(docid) + "  DELETED");
      return;
    }
    setString(find("docNum1"), "text", String.valueOf(docid));
    for (int i = 0; i < idxFields.length; i++) {
      final IndexableField[] fields = doc.getFields(idxFields[i]);
      if (fields.length == 0) {
        addFieldRow(table, idxFields[i], null, docid, null);
        continue;
      }
      for (int j = 0; j < fields.length; j++) {
        addFieldRow(table, idxFields[i], fields[j], docid, (TFIDFSimilarity) sim);
      }
    }
    doLayout(table);
  }

  Font courier = null;

  private void addFieldRow(final Object table, final String fName, final IndexableField ixf,
    final int docid, final TFIDFSimilarity sim) {
    final Object row = create("row");
    add(table, row);
    putProperty(row, "field", ixf);
    putProperty(row, "fName", fName);
    Object cell = create("cell");
    setString(cell, "text", fName);
    add(row, cell);
    cell = create("cell");
    final Field f = (Field) ixf;
    String flags = Util.fieldFlags(f, infos.fieldInfo(fName));
    boolean hasVectors = false;
    try {
      hasVectors = ar.getTermVector(docid, fName) != null;
    }
    catch (final Exception e) {
      // ignore
    }
    // consider skipping this field altogether?
    if (ixf == null && !hasVectors) {
      setBoolean(cell, "enabled", false);
    }
    if (hasVectors) {
      flags = flags.substring(0, 7) + "V" + flags.substring(8);
    }
    setString(cell, "text", flags);
    setFont(cell, "font", courier);
    setChoice(cell, "alignment", "center");
    add(row, cell);
    cell = create("cell");
    final FieldInfo info = infos.fieldInfo(fName);
    if (f != null) {
      try {
        if (ar != null && info.hasNorms()) {
          final DocValues norms = ar.normValues(fName);
          final String val = Util.normsToString(norms, fName, docid, sim);
          setString(cell, "text", val);
        }
        else {
          setString(cell, "text", "---");
          setBoolean(cell, "enabled", false);
        }
      }
      catch (final IOException ioe) {
        ioe.printStackTrace();
        setString(cell, "text", "!?!");
      }
    }
    else {
      setString(cell, "text", "---");
      setBoolean(cell, "enabled", false);
    }
    add(row, cell);
    cell = create("cell");
    if (f != null) {
      String text = f.stringValue();
      if (text == null) {
        if (f.binaryValue() != null) {
          text = Util.bytesToHex(f.binaryValue(), false);
        }
        else {
          text = "(null)";
        }
      }
      Decoder dec = decoders.get(f.name());
      if (dec == null) {
        dec = defDecoder;
      }
      try {
        if (f.fieldType().stored()/* && f.numericValue() == null && f.binaryValue() == null */) {
          text = dec.decodeStored(f.name(), f);
        }
        else {
          text = dec.decodeTerm(f.name(), text);
        }
      }
      catch (final Throwable e) {
        setColor(cell, "foreground", Color.RED);
      }
      setString(cell, "text", Util.escape(text));
    }
    else {
      setString(cell, "text", "<not present or not stored>");
      setBoolean(cell, "enabled", false);
    }
    add(row, cell);
  }

  public void showTV(final Object table) {
    final Object row = getSelectedItem(table);
    if (row == null) {
      return;
    }
    if (ar == null) {
      showStatus(MSG_NOINDEX);
      return;
    }
    final Integer DocId = (Integer) getProperty(table, "docNum");
    if (DocId == null) {
      showStatus("Missing Doc. Id.");
      return;
    }
    final SlowThread st = new SlowThread(this) {

      @Override
      public void execute() {
        try {
          final String fName = (String) getProperty(row, "fName");
          final Terms tfv = ar.getTermVector(DocId.intValue(), fName);
          if (tfv == null) {
            showStatus("Term Vector not available in field " + fName + " for this doc.");
            return;
          }
          final List<IntPair> tvs = TermVectorMapper.map(tfv, null, true, false);
          if (tvs == null || tvs.isEmpty()) {
            showStatus("Term Vector not available (empty).");
            return;
          }
          final Object dialog = addComponent(null, "/xml/vector.xml", null, null);
          setString(find(dialog, "fld"), "text", fName);
          final Object vTable = find(dialog, "vTable");
          Decoder dec = decoders.get(fName);
          if (dec == null) {
            dec = defDecoder;
          }
          Collections.sort(tvs, new IntPair.PairComparator(false, true));
          for (int i = 0; i < tvs.size(); i++) {
            final Object r = create("row");
            add(vTable, r);
            final IntPair ip = tvs.get(i);
            putProperty(r, "tf", ip);
            Object cell = create("cell");
            String s;
            try {
              s = dec.decodeTerm(fName, ip.text);
            }
            catch (final Throwable e) {
              s = ip.text;
              setColor(cell, "foreground", Color.RED);
            }
            setString(cell, "text", Util.escape(s));
            add(r, cell);
            cell = create("cell");
            setString(cell, "text", String.valueOf(ip.cnt));
            add(r, cell);
            cell = create("cell");
            if (ip.positions != null) {
              final StringBuilder sb = new StringBuilder();
              for (int k = 0; k < ip.positions.length; k++) {
                if (k > 0) {
                  sb.append(',');
                }
                sb.append(String.valueOf(ip.positions[k]));
              }
              setString(cell, "text", sb.toString());
            }
            add(r, cell);
            cell = create("cell");
            if (ip.starts != null) {
              final StringBuilder sb = new StringBuilder();
              for (int k = 0; k < ip.starts.length; k++) {
                if (k > 0) {
                  sb.append(',');
                }
                sb.append(ip.starts[k] + "-" + ip.ends[k]);
              }
              setString(cell, "text", sb.toString());
            }
            add(r, cell);
          }
          add(dialog);
        }
        catch (final Exception e) {
          e.printStackTrace();
          showStatus(e.getMessage());
        }
      }
    };
    if (slowAccess) {
      st.start();
    }
    else {
      st.execute();
    }
  }

  public void clipTV(final Object vTable) {
    final Object[] rows = getItems(vTable);
    final StringBuilder sb = new StringBuilder();
    for (int i = 0; i < rows.length; i++) {
      final IntPair tf = (IntPair) getProperty(rows[i], "tf");
      sb.append(tf.cnt + "\t" + tf.text);
      if (tf.positions != null) {
        sb.append("\t");
        for (int k = 0; k < tf.positions.length; k++) {
          if (k > 0) {
            sb.append(',');
          }
          sb.append(String.valueOf(tf.positions[k]));
        }
      }
      if (tf.starts != null) {
        if (tf.positions == null) {
          sb.append("\t");
        }
        sb.append("\t");
        for (int k = 0; k < tf.starts.length; k++) {
          if (k > 0) {
            sb.append(',');
          }
          sb.append(tf.starts[i] + "-" + tf.ends[i]);
        }
      }
      sb.append("\n");
    }
    final StringSelection sel = new StringSelection(sb.toString());
    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel, this);
  }

  public void actionExamineNorm(final Object table) throws Exception {
    final Object row = getSelectedItem(table);
    if (row == null) {
      return;
    }
    if (ir == null) {
      showStatus(MSG_NOINDEX);
      return;
    }
    final Field f = (Field) getProperty(row, "field");
    if (f == null) {
      showStatus("No data available for this field");
      return;
    }
    final FieldInfo info = infos.fieldInfo(f.name());
    if (!info.isIndexed()) {
      showStatus("Cannot examine norm value - this field is not indexed.");
      return;
    }
    if (!info.hasNorms()) {
      showStatus("Cannot examine norm value - this field has no norms.");
      return;
    }
    final Object dialog = addComponent(null, "/xml/fnorm.xml", null, null);
    final Integer docNum = (Integer) getProperty(table, "docNum");
    putProperty(dialog, "docNum", getProperty(table, "docNum"));
    putProperty(dialog, "field", f);
    final Object curNorm = find(dialog, "curNorm");
    final Object newNorm = find(dialog, "newNorm");
    final Object encNorm = find(dialog, "encNorm");
    final Object doc = find(dialog, "docNum");
    final Object fld = find(dialog, "fld");
    final Object srchOpts = find("srchOptTabs");
    final Similarity sim = createSimilarity(srchOpts);
    TFIDFSimilarity s = null;
    if (sim != null && sim instanceof TFIDFSimilarity) {
      s = (TFIDFSimilarity) sim;
    }
    else {
      s = defaultSimilarity;
    }
    setString(doc, "text", String.valueOf(docNum.intValue()));
    setString(fld, "text", f.name());
    putProperty(dialog, "similarity", s);
    if (ar != null) {
      try {
        final DocValues norms = ar.normValues(f.name());
        final byte curBVal = (byte) norms.getSource().getInt(docNum.intValue());
        final float curFVal = Util.decodeNormValue(curBVal, f.name(), s);
        setString(curNorm, "text", String.valueOf(curFVal));
        setString(newNorm, "text", String.valueOf(curFVal));
        setString(encNorm, "text", String.valueOf(curFVal) + " (0x" + Util.byteToHex(curBVal) + ")");
      }
      catch (final Exception e) {
        e.printStackTrace();
        errorMsg("Error reading norm: " + e.toString());
        return;
      }
    }
    add(dialog);
    displayNewNorm(dialog);
  }

  public void displayNewNorm(final Object dialog) {
    final Object newNorm = find(dialog, "newNorm");
    final Object encNorm = find(dialog, "encNorm");
    final Field f = (Field) getProperty(dialog, "field");
    TFIDFSimilarity s = (TFIDFSimilarity) getProperty(dialog, "similarity");
    final Object sim = find(dialog, "sim");
    final String simClassString = getString(sim, "text");
    if (simClassString != null && simClassString.trim().length() > 0
      && !simClassString.equals(defaultSimilarity.getClass().getName())) {
      try {
        final Class cls = Class.forName(simClassString.trim());
        if (TFIDFSimilarity.class.isAssignableFrom(cls)) {
          s = (TFIDFSimilarity) cls.newInstance();
          putProperty(dialog, "similarity", s);
        }
      }
      catch (final Throwable t) {
        t.printStackTrace();
        showStatus("Invalid similarity class " + simClassString + ", using DefaultSimilarity.");
      }
    }
    if (s == null) {
      s = defaultSimilarity;
    }
    setString(sim, "text", s.getClass().getName());
    try {
      final float newFVal = Float.parseFloat(getString(newNorm, "text"));
      final byte newBVal = Util.encodeNormValue(newFVal, f.name(), s);
      final float encFVal = Util.decodeNormValue(newBVal, f.name(), s);
      setString(encNorm, "text", String.valueOf(encFVal) + " (0x" + Util.byteToHex(newBVal) + ")");
      putProperty(dialog, "newNorm", new Float(newFVal));
      doLayout(dialog);
    }
    catch (final Exception e) {
      // XXX eat silently
    }
  }

  public void showTField(final Object table) {
    final Object row = getSelectedItem(table);
    if (row == null) {
      return;
    }
    if (ir == null) {
      showStatus(MSG_NOINDEX);
      return;
    }
    final Field f = (Field) getProperty(row, "field");
    if (f == null) {
      showStatus("No data available for this field");
      return;
    }
    final Object dialog = addComponent(null, "/xml/field.xml", null, null);
    final Object fName = find(dialog, "fld");
    putProperty(dialog, "f", f);
    setString(fName, "text", f.name());
    add(dialog);
    _showData(dialog);
  }

  public void _showData(final Object dialog) {
    final Object fDataText = find(dialog, "fDataText");
    final Field f = (Field) getProperty(dialog, "f");
    String value = null;
    String enc = "cbUtf";
    final Object choice = getSelectedItem(find(dialog, "cbData"));
    final String selEnc = getString(choice, "name");
    boolean warn = false;
    if (selEnc != null) {
      enc = selEnc;
    }
    int len = 0;
    byte[] data = null;
    if (f.binaryValue() != null) {
      final BytesRef bytes = f.binaryValue();
      data = new byte[bytes.length];
      System.arraycopy(bytes.bytes, bytes.offset, data, 0, bytes.length);
    }
    else if (f.stringValue() != null) {
      try {
        data = f.stringValue().getBytes("UTF-8");
      }
      catch (final UnsupportedEncodingException uee) {
        warn = true;
        uee.printStackTrace();
        data = f.stringValue().getBytes();
      }
    }
    if (data == null) {
      data = new byte[0];
    }
    if (enc.equals("cbHex")) {
      setString(find(dialog, "unit"), "text", " bytes");
      value = Util.bytesToHex(data, 0, data.length, true);
      len = data.length;
    }
    else if (enc.equals("cbUtf")) {
      setString(find(dialog, "unit"), "text", " UTF-8 characters");
      value = f.stringValue();
      if (value != null) {
        len = value.length();
      }
    }
    else if (enc.equals("cbDef")) {
      setString(find(dialog, "unit"), "text", " characters");
      value = new String(data);
      len = value.length();
    }
    else if (enc.equals("cbDate")) {
      try {
        final Date d = DateTools.stringToDate(f.stringValue());
        value = d.toString();
        len = 1;
      }
      catch (final Exception e) {
        warn = true;
        value = Util.bytesToHex(data, 0, data.length, true);
      }
    }
    else if (enc.equals("cbLong")) {
      try {
        final long num = NumericUtils.prefixCodedToLong(new BytesRef(f.stringValue()));
        value = String.valueOf(num);
        len = 1;
      }
      catch (final Exception e) {
        warn = true;
        value = Util.bytesToHex(data, 0, data.length, true);
      }
    }
    else if (enc.equals("cbNum")) {
      if (f.numericValue() != null) {
        value =
          f.numericValue().toString() + " (" + f.numericValue().getClass().getSimpleName() + ")";
      }
      else {
        warn = true;
        value = Util.bytesToHex(data, 0, data.length, true);
      }
    }
    else if (enc.equals("cbInt")) {
      if (data.length % 4 == 0) {
        setString(find(dialog, "unit"), "text", " int values");
        len = data.length / 4;
        final StringBuilder sb = new StringBuilder();
        for (int k = 0; k < data.length; k += 4) {
          if (k > 0) {
            sb.append(',');
          }
          sb.append(String.valueOf(PayloadHelper.decodeInt(data, k)));
        }
        value = sb.toString();
      }
      else {
        warn = true;
        value = Util.bytesToHex(data, 0, data.length, true);
      }
    }
    else if (enc.equals("cbFloat")) {
      if (data.length % 4 == 0) {
        setString(find(dialog, "unit"), "text", " float values");
        len = data.length / 4;
        final StringBuilder sb = new StringBuilder();
        for (int k = 0; k < data.length; k += 4) {
          if (k > 0) {
            sb.append(',');
          }
          sb.append(String.valueOf(PayloadHelper.decodeFloat(data, k)));
        }
        value = sb.toString();
      }
      else {
        warn = true;
        value = Util.bytesToHex(data, 0, data.length, true);
      }
    }
    setString(fDataText, "text", value);
    setString(find(dialog, "len"), "text", String.valueOf(len));
    if (warn) {
      setBoolean(fDataText, "enabled", false);
      errorMsg(MSG_CONV_ERROR);
    }
    else {
      setBoolean(fDataText, "enabled", true);
    }
  }

  public void saveField(final Object table) {
    final Object row = getSelectedItem(table);
    if (row == null) {
      return;
    }
    if (ir == null) {
      showStatus(MSG_NOINDEX);
      return;
    }
    final Field f = (Field) getProperty(row, "field");
    if (f == null) {
      showStatus("No data available for this field");
      return;
    }
    final JFileChooser fd = new JFileChooser();
    fd.setDialogType(JFileChooser.SAVE_DIALOG);
    fd.setDialogTitle("Save field content to a file");
    fd.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    fd.setFileHidingEnabled(false);
    if (this.baseDir != null) {
      fd.setCurrentDirectory(new File(this.baseDir));
    }
    else {
      fd.setCurrentDirectory(new File(System.getProperty("user.dir")));
    }
    final int res = fd.showSaveDialog(this);
    File iFile = null;
    if (res == JFileChooser.APPROVE_OPTION) {
      iFile = fd.getSelectedFile();
    }
    if (iFile == null) {
      return;
    }
    if (iFile.exists() && iFile.isDirectory()) {
      errorMsg("Can't overwrite a directory.");
      return;
    }
    Object progress = null;
    try {
      byte[] data = null;
      if (f.binaryValue() != null) {
        final BytesRef bytes = f.binaryValue();
        data = new byte[bytes.length];
        System.arraycopy(bytes.bytes, bytes.offset, data, 0, bytes.length);
      }
      else {
        try {
          data = f.stringValue().getBytes("UTF-8");
        }
        catch (final UnsupportedEncodingException uee) {
          uee.printStackTrace();
          errorMsg(uee.toString());
          data = f.stringValue().getBytes();
        }
      }
      if (data == null || data.length == 0) {
        showStatus("No data available");
        return;
      }
      progress = addComponent(null, "/xml/progress.xml", null, null);
      setString(find(progress, "msg"), "text", "Saving...");
      final Object bar = find(progress, "bar");
      setInteger(bar, "maximum", 100);
      final OutputStream os = new FileOutputStream(iFile);
      int delta = data.length / 100;
      if (delta == 0) {
        delta = 1;
      }
      add(progress);
      for (int i = 0; i < data.length; i++) {
        os.write(data[i]);
        if (i % delta == 0) {
          setInteger(bar, "value", i / delta);
        }
      }
      os.flush();
      os.close();
      setString(find(progress, "msg"), "text", "Done!");
      try {
        Thread.sleep(1000);
      }
      catch (final Exception e) {
      }
      ;
    }
    catch (final IOException ioe) {
      ioe.printStackTrace();
      errorMsg("Can't save: " + ioe);
      return;
    }
    finally {
      if (progress != null) {
        remove(progress);
      }
    }
  }

  public void clipCopyFields(final Object table) {
    final Object[] rows = getSelectedItems(table);
    if (rows == null || rows.length == 0) {
      return;
    }
    final Document doc = (Document) getProperty(table, "doc");
    if (doc == null) {
      return;
    }
    final StringBuffer sb = new StringBuffer();
    for (int i = 0; i < rows.length; i++) {
      final Field f = (Field) getProperty(rows[i], "field");
      if (f == null) {
        continue;
      }
      if (i > 0) {
        sb.append('\n');
      }
      sb.append(f.toString());
    }
    final StringSelection sel = new StringSelection(sb.toString());
    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel, this);
  }

  public void clipCopyDoc(final Object table) {
    final Document doc = (Document) getProperty(table, "doc");
    if (doc == null) {
      return;
    }
    final StringBuffer sb = new StringBuffer();
    final Object[] rows = getItems(table);
    for (int i = 0; i < rows.length; i++) {
      final Field f = (Field) getProperty(rows[i], "field");
      if (f == null) {
        continue;
      }
      if (i > 0) {
        sb.append('\n');
      }
      sb.append(f.toString());
    }
    final StringSelection sel = new StringSelection(sb.toString());
    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel, this);
  }

  public void showFirstTerm(final Object fCombo, final Object fText) {
    if (ir == null) {
      showStatus(MSG_NOINDEX);
      return;
    }
    final SlowThread st = new SlowThread(this) {

      @Override
      public void execute() {
        try {
          final String fld = getString(fCombo, "text");
          final Terms terms = MultiFields.getTerms(ir, fld);
          final TermsEnum te = terms.iterator(null);
          putProperty(fCombo, "te", te);
          putProperty(fCombo, "teField", fld);
          final BytesRef term = te.next();
          _showTerm(fCombo, fText, new Term(fld, term));
        }
        catch (final Exception e) {
          e.printStackTrace();
          showStatus(e.getMessage());
        }
      }
    };
    if (slowAccess) {
      st.start();
    }
    else {
      st.execute();
    }
  }

  public void showNextTerm(final Object fCombo, final Object fText) {
    if (ir == null) {
      showStatus(MSG_NOINDEX);
      return;
    }
    final SlowThread st = new SlowThread(this) {

      @Override
      public void execute() {
        try {
          String text;
          text = getString(fText, "text");
          if (text == null || text.trim().equals("")) {
            text = "";
          }
          if (text.length() == 0) {
            showFirstTerm(fCombo, fText);
            return;
          }
          TermsEnum te = (TermsEnum) getProperty(fCombo, "te");
          String fld = getString(fCombo, "text");
          final String teField = (String) getProperty(fCombo, "teField");
          SeekStatus status;
          BytesRef rawTerm = null;
          if (te != null) {
            rawTerm = te.term();
          }
          final String rawString = rawTerm != null ? rawTerm.utf8ToString() : null;
          if (te == null || !teField.equals(fld) || !text.equals(rawString)) {
            final Terms terms = MultiFields.getTerms(ir, fld);
            te = terms.iterator(null);
            putProperty(fCombo, "te", te);
            putProperty(fCombo, "teField", fld);
            status = te.seekCeil(new BytesRef(text));
            if (status.equals(SeekStatus.FOUND)) {
              rawTerm = te.term();
            }
            else {
              rawTerm = null;
            }
          }
          else {
            rawTerm = te.next();
          }
          if (rawTerm == null) { // proceed to next field
            int idx = fn.indexOf(fld);
            while (idx < fn.size() - 1) {
              idx++;
              setInteger(fCombo, "selected", idx);
              fld = fn.get(idx);
              final Terms terms = MultiFields.getTerms(ir, fld);
              if (terms == null) {
                continue;
              }
              te = terms.iterator(null);
              rawTerm = te.next();
              putProperty(fCombo, "te", te);
              putProperty(fCombo, "teField", fld);
              break;
            }
          }
          if (rawTerm == null) {
            showStatus("No more terms");
            return;
          }
          // Term t = new Term(fld, term.utf8ToString());
          _showTerm(fCombo, fText, new Term(fld, rawTerm));
        }
        catch (final Exception e) {
          e.printStackTrace();
          showStatus(e.getMessage());
        }
      }
    };
    if (slowAccess) {
      st.start();
    }
    else {
      st.execute();
    }
  }

  public void showTerm(final Object fCombo, final Object fText) {
    if (ir == null) {
      showStatus(MSG_NOINDEX);
      return;
    }
    final SlowThread st = new SlowThread(this) {

      @Override
      public void execute() {
        try {
          String text;
          final Term rawTerm = (Term) getProperty(fText, "term");
          text = getString(fText, "text");
          if (rawTerm != null) {
            final String s = (String) getProperty(fText, "decText");
            if (s.equals(text)) {
              text = rawTerm.text();
            }
          }
          final String fld = getString(fCombo, "text");
          if (text == null || text.trim().equals("")) {
            return;
          }
          Term t = new Term(fld, text);
          if (ir.docFreq(t) == 0) { // missing term
            final Terms terms = MultiFields.getTerms(ir, fld);
            final TermsEnum te = terms.iterator(null);
            te.seekCeil(new BytesRef(text));
            t = new Term(fld, te.term().utf8ToString());
          }
          _showTerm(fCombo, fText, t);
        }
        catch (final Exception e) {
          e.printStackTrace();
          showStatus(e.getMessage());
        }
      }
    };
    if (slowAccess) {
      st.start();
    }
    else {
      st.execute();
    }
  }

  private void _showTerm(final Object fCombo, final Object fText, final Term t) {
    if (t == null) {
      showStatus("No terms?!");
      return;
    }
    if (ir == null) {
      showStatus(MSG_NOINDEX);
      return;
    }
    final Object[] choices = getItems(fCombo);
    for (int i = 0; i < choices.length; i++) {
      if (t.field().equals(getString(choices[i], "text"))) {
        setInteger(fCombo, "selected", i);
        break;
      }
    }
    Decoder dec = decoders.get(t.field());
    if (dec == null) {
      dec = defDecoder;
    }
    String s = null;
    boolean decodeErr = false;
    try {
      s = dec.decodeTerm(t.field(), t.text());
    }
    catch (final Throwable e) {
      s = e.getMessage();
      decodeErr = true;
    }
    setString(fText, "text", t.text());
    final Object rawText = find("decText");
    if (!s.equals(t.text())) {
      setString(rawText, "text", s);
      if (decodeErr) {
        setColor(rawText, "foreground", Color.RED);
      }
      else {
        setColor(rawText, "foreground", Color.BLUE);
      }
    }
    else {
      setString(rawText, "text", "");
      setColor(rawText, "foreground", Color.BLACK);
    }
    putProperty(fText, "term", t);
    putProperty(fText, "decText", s);
    putProperty(fText, "td", null);
    setString(find("tdNum"), "text", "?");
    setString(find("tFreq"), "text", "?");
    final SlowThread st = new SlowThread(this) {

      @Override
      public void execute() {
        Object dFreq = find("dFreq");
        try {
          final int freq = ir.docFreq(t);
          setString(dFreq, "text", String.valueOf(freq));
          dFreq = find("tdMax");
          setString(dFreq, "text", String.valueOf(freq));
        }
        catch (final Exception e) {
          e.printStackTrace();
          showStatus(e.getMessage());
          setString(dFreq, "text", "?");
        }
      }
    };
    if (slowAccess) {
      st.start();
    }
    else {
      st.execute();
    }
  }

  public void showFirstTermDoc(final Object fText) {
    final Term t = (Term) getProperty(fText, "term");
    if (t == null) {
      return;
    }
    if (ir == null) {
      showStatus(MSG_NOINDEX);
      return;
    }
    if (ar == null) {
      errorMsg(MSG_LUCENE3828);
      return;
    }
    final SlowThread st = new SlowThread(this) {

      @Override
      public void execute() {
        try {

          final DocsAndPositionsEnum td = ar.termPositionsEnum(new Term(t.field(), t.bytes()));
          if (td == null) {
            showStatus("No such term: " + t);
            return;
          }
          if (td.nextDoc() == DocIdSetIterator.NO_MORE_DOCS) {
            showStatus("No documents with this term: " + t + " (NO_MORE_DOCS)");
            return;
          }
          setString(find("tdNum"), "text", "1");
          putProperty(fText, "td", td);
          _showTermDoc(fText, td);
        }
        catch (final Exception e) {
          e.printStackTrace();
          showStatus(e.getMessage());
        }
      }
    };
    if (slowAccess) {
      st.start();
    }
    else {
      st.execute();
    }
  }

  public void showNextTermDoc(final Object fText) {
    final Term t = (Term) getProperty(fText, "term");
    if (t == null) {
      return;
    }
    if (ir == null) {
      showStatus(MSG_NOINDEX);
      return;
    }
    final SlowThread st = new SlowThread(this) {

      @Override
      public void execute() {
        try {
          final DocsEnum td = (DocsEnum) getProperty(fText, "td");
          if (td == null) {
            showFirstTermDoc(fText);
            return;
          }
          if (td.nextDoc() == DocIdSetIterator.NO_MORE_DOCS) {
            showStatus("No more docs for this term");
            return;
          }
          final Object tdNum = find("tdNum");
          final String sCnt = getString(tdNum, "text");
          int cnt = 1;
          try {
            cnt = Integer.parseInt(sCnt);
          }
          catch (final Exception e) {
          }
          ;
          setString(tdNum, "text", String.valueOf(cnt + 1));
          _showTermDoc(fText, td);
        }
        catch (final Exception e) {
          e.printStackTrace();
          showStatus(e.getMessage());
        }
      }
    };
    if (slowAccess) {
      st.start();
    }
    else {
      st.execute();
    }
  }

  public void showPositions(final Object fText) {
    final Term t = (Term) getProperty(fText, "term");
    if (t == null) {
      return;
    }
    if (ir == null) {
      showStatus(MSG_NOINDEX);
      return;
    }
    if (ar == null) {
      errorMsg(MSG_LUCENE3828);
      return;
    }
    final SlowThread st = new SlowThread(this) {

      @Override
      public void execute() {

        try {
          final FieldInfo fi = infos.fieldInfo(t.field());
          boolean withOffsets = false;
          if (fi != null) {
            final IndexOptions opts = fi.getIndexOptions();
            if (opts != null) {
              if (opts.equals(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS)) {
                withOffsets = true;
              }
            }
          }
          final DocsEnum tdd = (DocsEnum) getProperty(fText, "td");
          if (tdd == null) {
            showStatus("Unknown document number.");
            return;
          }
          int flags = DocsAndPositionsEnum.FLAG_PAYLOADS;
          if (withOffsets) {
            flags |= DocsAndPositionsEnum.FLAG_OFFSETS;
          }

          final DocsAndPositionsEnum td = ar.termPositionsEnum(new Term(t.field(), t.bytes()));

          if (td == null) {
            showStatus("No position information available for this term.");
            return;
          }
          if (td.advance(tdd.docID()) != td.docID()) {
            showStatus("No position available for this doc and this term.");
            return;
          }
          final Object dialog = addComponent(null, "/xml/positions.xml", null, null);
          setString(find(dialog, "term"), "text", t.toString());
          final String docNum = getString(find("docNum"), "text");
          setString(find(dialog, "docNum"), "text", docNum);
          setString(find(dialog, "freq"), "text", String.valueOf(td.freq()));
          setString(find(dialog, "offs"), "text", String.valueOf(withOffsets));
          putProperty(dialog, "td", td);
          final Object pTable = find(dialog, "pTable");
          removeAll(pTable);
          final int freq = td.freq();
          for (int i = 0; i < freq; i++) {
            try {
              final int pos = td.nextPosition();
              final Object r = create("row");
              Object cell = create("cell");
              setString(cell, "text", String.valueOf(pos));
              add(r, cell);
              cell = create("cell");
              add(r, cell);
              if (withOffsets) {
                setString(cell, "text", td.startOffset() + "-" + td.endOffset());
              }
              else {
                setString(cell, "text", "---");
                setBoolean(cell, "enabled", false);
              }
              cell = create("cell");
              add(r, cell);
              final BytesRef payload = td.getPayload();
              if (payload != null) {
                putProperty(r, "payload", payload.clone());
              }
              add(pTable, r);
            }
            catch (final IOException ioe) {
              errorMsg("Error: " + ioe.toString());
              return;
            }
          }
          _showPayloads(dialog);
          add(dialog);
        }
        catch (final Exception e) {
          e.printStackTrace();
          showStatus(e.getMessage());
        }
      }
    };
    if (slowAccess) {
      st.start();
    }
    else {
      st.execute();
    }
  }

  public void _showPayloads(final Object dialog) {
    final Object cbPay = find(dialog, "cbPay");
    final Object choice = getSelectedItem(cbPay);
    String enc = "cbUtf";
    if (choice != null) {
      enc = getString(choice, "name");
    }
    final Object pTable = find(dialog, "pTable");
    final Object[] rows = getItems(pTable);
    boolean warn = false;
    for (int i = 0; i < rows.length; i++) {
      final BytesRef payload = (BytesRef) getProperty(rows[i], "payload");
      if (payload == null) {
        continue;
      }
      final Object cell = getItem(rows[i], 2);
      String curEnc = enc;
      if (enc.equals("cbInt") || enc.equals("cbFloat")) {
        if (payload.length % 4 != 0) {
          curEnc = "cbHex";
        }
      }
      String val = "?";
      if (curEnc.equals("cbUtf")) {
        try {
          val = new String(payload.bytes, payload.offset, payload.length, "UTF-8");
        }
        catch (final Exception e) {
          e.printStackTrace();
          val = new String(payload.bytes, payload.offset, payload.length);
          curEnc = "cbDef";
        }
      }
      else if (curEnc.equals("cbHex")) {
        val = Util.bytesToHex(payload.bytes, payload.offset, payload.length, false);
      }
      else if (curEnc.equals("cbDef")) {
        val = new String(payload.bytes, payload.offset, payload.length);
      }
      else if (curEnc.equals("cbInt")) {
        final StringBuilder sb = new StringBuilder();
        for (int k = payload.offset; k < payload.offset + payload.length; k += 4) {
          if (k > 0) {
            sb.append(',');
          }
          sb.append(String.valueOf(PayloadHelper.decodeInt(payload.bytes, k)));
        }
        val = sb.toString();
      }
      else if (curEnc.equals("cbFloat")) {
        final StringBuilder sb = new StringBuilder();
        for (int k = payload.offset; k < payload.offset + payload.length; k += 4) {
          if (k > 0) {
            sb.append(',');
          }
          sb.append(String.valueOf(PayloadHelper.decodeFloat(payload.bytes, k)));
        }
        val = sb.toString();
      }
      setString(cell, "text", val);
      if (!curEnc.equals(enc)) {
        setBoolean(cell, "enabled", false);
        warn = true;
      }
      else {
        setBoolean(cell, "enabled", true);
      }
    }
    if (warn) {
      errorMsg(MSG_CONV_ERROR);
    }
  }

  public void clipPositions(final Object pTable) {
    final Object[] rows = getItems(pTable);
    final StringBuilder sb = new StringBuilder();
    for (int i = 0; i < rows.length; i++) {
      if (i > 0) {
        sb.append('\n');
      }
      final Object[] cells = getItems(rows[i]);
      for (int k = 0; k < cells.length; k++) {
        if (k > 0) {
          sb.append('\t');
        }
        sb.append(getString(cells[k], "text"));
      }
    }
    final StringSelection sel = new StringSelection(sb.toString());
    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel, this);
  }

  public void showAllTermDoc(final Object fText) {
    final Term t = (Term) getProperty(fText, "term");
    if (t == null) {
      return;
    }
    if (ir == null) {
      showStatus("MSG_NOINDEX");
      return;
    }
    final Object tabpane = find("maintpane");
    setInteger(tabpane, "selected", 2);
    final Object qField = find("qField");
    setString(qField, "text", t.field() + ":" + t.text());
    final Object qFieldParsed = find("qFieldParsed");
    final Object ckScoreRes = find("ckScoreRes");
    final Object ckOrderRes = find("ckOrderRes");
    final Object cntRepeat = find("cntRepeat");
    final boolean scoreRes = getBoolean(ckScoreRes, "selected");
    final boolean orderRes = getBoolean(ckOrderRes, "selected");
    final int repeat = Integer.parseInt(getString(cntRepeat, "text"));
    final Query q = new TermQuery(t);
    setString(qFieldParsed, "text", q.toString());
    final SlowThread st = new SlowThread(this) {

      @Override
      public void execute() {
        IndexSearcher is = null;
        try {
          is = new IndexSearcher(ir);
          final Object sTable = find("sTable");
          removeAll(sTable);
          final AllHitsCollector ahc = new AllHitsCollector(orderRes, scoreRes);
          _search(q, is, ahc, sTable, repeat);
        }
        catch (final Exception e) {
          e.printStackTrace();
          errorMsg(e.getMessage());
        }
        finally {
          if (is != null) {
            is = null;
          }
        }
      }
    };
    if (slowAccess) {
      st.start();
    }
    else {
      st.execute();
    }
  }

  public Analyzer createAnalyzer(final Object srchOpts) {
    Analyzer res = null;
    String sAType = getString(find(srchOpts, "cbType"), "text");
    if (sAType.trim().equals("")) {
      sAType = "org.apache.lucene.analysis.standard.StandardAnalyzer";
      setString(find("cbType"), "text", sAType);
    }
    String arg = getString(find(srchOpts, "snoName"), "text");
    if (arg == null) {
      arg = "";
    }
    try {
      Constructor zeroArg = null, zeroArgV = null, oneArg = null, oneArgV = null;
      try {
        zeroArgV = Class.forName(sAType).getConstructor(new Class[] { Version.class });
      }
      catch (final NoSuchMethodException e) {
        zeroArgV = null;
        try {
          zeroArg = Class.forName(sAType).getConstructor(new Class[0]);
        }
        catch (final NoSuchMethodException e1) {
          zeroArg = null;
        }
      }
      try {
        oneArgV = Class.forName(sAType).getConstructor(new Class[] { Version.class, String.class });
      }
      catch (final NoSuchMethodException e) {
        oneArgV = null;
        try {
          oneArg = Class.forName(sAType).getConstructor(new Class[] { String.class });
        }
        catch (final NoSuchMethodException e1) {
          oneArg = null;
        }
      }
      if (arg.length() == 0) {
        if (zeroArgV != null) {
          res = (Analyzer) zeroArgV.newInstance(Version.LUCENE_CURRENT);
        }
        else if (zeroArg != null) {
          res = (Analyzer) zeroArg.newInstance();
        }
        else if (oneArgV != null) {
          res = (Analyzer) oneArgV.newInstance(new Object[] { Version.LUCENE_CURRENT, arg });
        }
        else if (oneArg != null) {
          res = (Analyzer) oneArg.newInstance(new Object[] { arg });
        }
        else {
          throw new Exception("Must have a zero-arg or (Version) or (Version, String) constructor");
        }
      }
      else {
        if (oneArgV != null) {
          res = (Analyzer) oneArgV.newInstance(new Object[] { Version.LUCENE_CURRENT, arg });
        }
        else if (oneArg != null) {
          res = (Analyzer) oneArg.newInstance(new Object[] { arg });
        }
        else if (zeroArgV != null) {
          res = (Analyzer) zeroArgV.newInstance(new Object[] { Version.LUCENE_CURRENT });
        }
        else if (zeroArg != null) {
          res = (Analyzer) zeroArg.newInstance(new Object[0]);
        }
        else {
          throw new Exception("Must have a zero-arg or (String) constructor");
        }
      }
    }
    catch (final Exception e) {
      e.printStackTrace();
      errorMsg("Analyzer '" + sAType + "' error: " + e.getMessage());
      return null;
    }
    Prefs.setProperty(Prefs.P_ANALYZER, res.getClass().getName());
    return res;
  }

  protected String getDefaultField(final Object srchOptTabs) {
    String defField = getString(find(srchOptTabs, "defFld"), "text");
    if (defField == null || defField.trim().equals("")) {
      if (ir != null) {
        defField = idxFields[0];
        setString(find(srchOptTabs, "defFld"), "text", defField);
      }
      else {
        defField = "DEFAULT";
      }
    }
    return defField;
  }

  /**
   * Create a Query instance that corresponds to values selected in the UI, such as analyzer class name and arguments,
   * and default field.
   *
   * @return
   */
  public Query createQuery(final String queryString) throws Exception {
    final Object srchOpts = find("srchOptTabs");
    final Analyzer analyzer = createAnalyzer(srchOpts);
    if (analyzer == null) {
      return null;
    }
    final String defField = getDefaultField(srchOpts);
    final QueryParser qp = new QueryParser(Version.LUCENE_CURRENT, defField, analyzer);
    final Object ckXmlParser = find(srchOpts, "ckXmlParser");
    final Object ckWild = find(srchOpts, "ckWild");
    final Object ckPosIncr = find(srchOpts, "ckPosIncr");
    final Object ckLoExp = find(srchOpts, "ckLoExp");
    final Object cbDateRes = find(srchOpts, "cbDateRes");
    final DateTools.Resolution resolution = Util.getResolution(getString(cbDateRes, "text"));
    final Object cbOp = find(srchOpts, "cbOp");
    final Object bqMaxCount = find(srchOpts, "bqMaxCount");
    int maxCount = 1024;
    try {
      maxCount = Integer.parseInt(getString(bqMaxCount, "text"));
    }
    catch (final Exception e) {
      e.printStackTrace();
      showStatus("Invalid BooleanQuery max clause count, using default 1024");
    }
    QueryParser.Operator op;
    BooleanQuery.setMaxClauseCount(maxCount);
    final String opString = getString(cbOp, "text");
    if (opString.equalsIgnoreCase("OR")) {
      op = QueryParserBase.OR_OPERATOR;
    }
    else {
      op = QueryParserBase.AND_OPERATOR;
    }
    qp.setAllowLeadingWildcard(getBoolean(ckWild, "selected"));
    qp.setEnablePositionIncrements(getBoolean(ckPosIncr, "selected"));
    qp.setLowercaseExpandedTerms(getBoolean(ckLoExp, "selected"));
    qp.setDateResolution(resolution);
    qp.setDefaultOperator(op);
    if (getBoolean(ckXmlParser, "selected")) {

      final CoreParser cp = createParser(defField, analyzer);
      final Query q = cp.parse(new ByteArrayInputStream(queryString.getBytes("UTF-8")));
      return q;
    }
    else {
      return qp.parse(queryString);
    }
  }

  private CoreParser createParser(final String defaultField, final Analyzer analyzer)
    throws Exception {
    if (xmlQueryParserFactoryClassName == null) {
      // Use the default
      return new CorePlusExtensionsParser(defaultField, analyzer);
    }
    // Use a user-defined parser (classname passed in -xmlQueryParserFactory command-line parameter
    final XmlQueryParserFactory parserFactory =
      (XmlQueryParserFactory) Class.forName(xmlQueryParserFactoryClassName).newInstance();
    return parserFactory.createParser(defaultField, analyzer);
  }

  public Similarity createSimilarity(final Object srchOpts) {
    final Object ckSimDef = find(srchOpts, "ckSimDef");
    final Object ckSimSweet = find(srchOpts, "ckSimSweet");
    final Object ckSimOther = find(srchOpts, "ckSimOther");
    final Object simClass = find(srchOpts, "simClass");
    final Object ckSimCust = find(srchOpts, "ckSimCust");
    if (getBoolean(ckSimDef, "selected")) {
      return new DefaultSimilarity();
    }
    else if (getBoolean(ckSimSweet, "selected")) {
      return new SweetSpotSimilarity();
    }
    else if (getBoolean(ckSimOther, "selected")) {
      try {
        final Class clazz = Class.forName(getString(simClass, "text"));
        if (Similarity.class.isAssignableFrom(clazz)) {
          final Similarity sim = (Similarity) clazz.newInstance();
          return sim;
        }
        else {
          throw new Exception("Not a subclass of Similarity: " + clazz.getName());
        }
      }
      catch (final Exception e) {
        e.printStackTrace();
        showStatus("ERROR: invalid Similarity, using default");
        setBoolean(ckSimDef, "selected", true);
        setBoolean(ckSimOther, "selected", false);
        return new DefaultSimilarity();
      }
    }
    else if (getBoolean(ckSimCust, "selected")) {
      return similarity;
    }
    else {
      return new DefaultSimilarity();
    }
  }

  public AccessibleHitCollector createCollector(final Object srchOpts) throws Exception {
    final Object ckNormRes = find(srchOpts, "ckNormRes");
    final Object ckAllRes = find(srchOpts, "ckAllRes");
    final Object ckLimRes = find(srchOpts, "ckLimRes");
    final Object ckLimTime = find(srchOpts, "ckLimTime");
    final Object limTime = find(srchOpts, "limTime");
    final Object ckLimCount = find(srchOpts, "ckLimCount");
    final Object limCount = find(srchOpts, "limCount");
    final Object ckScoreRes = find(srchOpts, "ckScoreRes");
    final Object ckOrderRes = find(srchOpts, "ckOrderRes");
    final boolean scoreRes = getBoolean(ckScoreRes, "selected");
    final boolean orderRes = getBoolean(ckOrderRes, "selected");
    final Collector hc = null;
    if (getBoolean(ckNormRes, "selected")) {
      return new AccessibleTopHitCollector(1000, orderRes, scoreRes);
    }
    else if (getBoolean(ckAllRes, "selected")) {
      return new AllHitsCollector(orderRes, scoreRes);
    }
    else if (getBoolean(ckLimRes, "selected")) {
      // figure out the type
      if (getBoolean(ckLimCount, "selected")) {
        final int lim = Integer.parseInt(getString(limCount, "text"));
        return new CountLimitedHitCollector(lim, orderRes, scoreRes);
      }
      else if (getBoolean(ckLimTime, "selected")) {
        final int lim = Integer.parseInt(getString(limTime, "text"));
        return new IntervalLimitedCollector(lim, orderRes, scoreRes);
      }
      else {
        throw new Exception("Unknown LimitedHitCollector type");
      }
    }
    else {
      throw new Exception("Unknown HitCollector type");
    }
  }

  public void explainStructure(final Object qTabs) {
    final Object qField = find("qField");
    final String queryS = getString(qField, "text");
    if (queryS.trim().equals("")) {
      showStatus("Empty query");
      return;
    }
    showParsed();
    final int idx = getSelectedIndex(qTabs);
    Query q = null;
    if (idx == 0) {
      q = (Query) getProperty(qField, "qParsed");
    }
    else {
      q = (Query) getProperty(qField, "qRewritten");
    }
    final Object dialog = addComponent(this, "/xml/qexplain.xml", null, null);
    final Object tree = find(dialog, "qTree");
    _explainStructure(tree, q);
  }

  private void _explainStructure(final Object parent, final Query q) {
    String clazz = q.getClass().getName();
    if (clazz.startsWith("org.apache.lucene.")) {
      clazz = "lucene." + q.getClass().getSimpleName();
    }
    else if (clazz.startsWith("org.apache.solr.")) {
      clazz = "solr." + q.getClass().getSimpleName();
    }
    final float boost = q.getBoost();
    final Object n = create("node");
    add(parent, n);
    String msg = clazz;
    if (boost != 1.0f) {
      msg += ": boost=" + df.format(boost);
    }
    setFont(n, getFont().deriveFont(Font.BOLD));
    setString(n, "text", msg);
    if (clazz.equals("lucene.TermQuery")) {
      final Object n1 = create("node");
      final Term t = ((TermQuery) q).getTerm();
      setString(n1, "text", "Term: field='" + t.field() + "' text='" + t.text() + "'");
      add(n, n1);
    }
    else if (clazz.equals("lucene.BooleanQuery")) {
      final BooleanQuery bq = (BooleanQuery) q;
      final BooleanClause[] clauses = bq.getClauses();
      final int max = BooleanQuery.getMaxClauseCount();
      Object n1 = create("node");
      String descr = "clauses=" + clauses.length + ", maxClauses=" + max;
      if (bq.isCoordDisabled()) {
        descr += ", coord=false";
      }
      if (bq.getMinimumNumberShouldMatch() > 0) {
        descr += ", minShouldMatch=" + bq.getMinimumNumberShouldMatch();
      }
      setString(n1, "text", descr);
      add(n, n1);
      for (int i = 0; i < clauses.length; i++) {
        n1 = create("node");
        String occur;
        final Occur occ = clauses[i].getOccur();
        if (occ.equals(Occur.MUST)) {
          occur = "MUST";
        }
        else if (occ.equals(Occur.MUST_NOT)) {
          occur = "MUST_NOT";
        }
        else if (occ.equals(Occur.SHOULD)) {
          occur = "SHOULD";
        }
        else {
          occur = occ.toString();
        }
        setString(n1, "text", "Clause " + i + ": " + occur);
        add(n, n1);
        _explainStructure(n1, clauses[i].getQuery());
      }
    }
    else if (clazz.equals("lucene.PrefixQuery")) {
      Object n1 = create("node");
      final PrefixQuery pq = (PrefixQuery) q;
      final Term t = pq.getPrefix();
      setString(n1, "text", "Prefix: field='" + t.field() + "' text='" + t.text() + "'");
      add(n, n1);
      try {
        addTermsEnum(n, PrefixQuery.class, pq.getField(), pq);
      }
      catch (final Exception e) {
        e.printStackTrace();
        n1 = create("node");
        setString(n1, "text", "TermEnum: Exception " + e.getMessage());
        add(n, n1);
      }
    }
    else if (clazz.equals("lucene.PhraseQuery")) {
      final PhraseQuery pq = (PhraseQuery) q;
      setString(n, "text", getString(n, "text") + ", slop=" + pq.getSlop());
      final int[] pos = pq.getPositions();
      final Term[] terms = pq.getTerms();
      Object n1 = create("node");
      final StringBuffer sb = new StringBuffer("pos: [");
      for (int i = 0; i < pos.length; i++) {
        if (i > 0) {
          sb.append(',');
        }
        sb.append("" + pos[i]);
      }
      sb.append("]");
      setString(n1, "text", sb.toString());
      add(n, n1);
      for (int i = 0; i < terms.length; i++) {
        n1 = create("node");
        setString(n1, "text",
          "Term " + i + ": field='" + terms[i].field() + "' text='" + terms[i].text() + "'");
        add(n, n1);
      }
    }
    else if (clazz.equals("lucene.MultiPhraseQuery")) {
      final MultiPhraseQuery pq = (MultiPhraseQuery) q;
      setString(n, "text", getString(n, "text") + ", slop=" + pq.getSlop());
      final int[] pos = pq.getPositions();
      Object n1 = create("node");
      final StringBuffer sb = new StringBuffer("pos: [");
      for (int i = 0; i < pos.length; i++) {
        if (i > 0) {
          sb.append(',');
        }
        sb.append("" + pos[i]);
      }
      sb.append("]");
      setString(n1, "text", sb.toString());
      add(n, n1);
      n1 = create("node");
      System.err.println("MultiPhraseQuery is missing the public getTermArrays() :-(");
      setString(n1, "text", "toString: " + pq.toString());
      add(n, n1);
    }
    else if (clazz.equals("lucene.FuzzyQuery")) {
      final FuzzyQuery fq = (FuzzyQuery) q;
      Object n1 = create("node");
      setString(n1, "text", "field=" + fq.getField() + ", prefixLen=" + fq.getPrefixLength()
        + ", maxEdits=" + df.format(fq.getMaxEdits()));
      add(n, n1);
      try {
        addTermsEnum(n, FuzzyQuery.class, fq.getField(), fq);
      }
      catch (final Exception e) {
        e.printStackTrace();
        n1 = create("node");
        setString(n1, "text", "TermEnum: Exception " + e.getMessage());
        add(n, n1);
      }
    }
    else if (clazz.equals("lucene.WildcardQuery")) {
      final WildcardQuery wq = (WildcardQuery) q;
      final Term t = wq.getTerm();
      setString(n, "text", getString(n, "text") + ", term=" + t);
      final Automaton a = WildcardQuery.toAutomaton(t);
      addAutomaton(n, a);
    }
    else if (clazz.equals("lucene.TermRangeQuery")) {
      final TermRangeQuery rq = (TermRangeQuery) q;
      setString(n, "text", getString(n, "text") + ", inclLower=" + rq.includesLower()
        + ", inclUpper=" + rq.includesUpper());
      Object n1 = create("node");
      setString(n1, "text", "lowerTerm=" + rq.getField() + ":" + rq.getLowerTerm() + "'");
      add(n, n1);
      n1 = create("node");
      setString(n1, "text", "upperTerm=" + rq.getField() + ":" + rq.getUpperTerm() + "'");
      add(n, n1);
      try {
        addTermsEnum(n, TermRangeQuery.class, rq.getField(), rq);
      }
      catch (final Exception e) {
        e.printStackTrace();
        n1 = create("node");
        setString(n1, "text", "TermEnum: Exception " + e.getMessage());
        add(n, n1);
      }
    }
    else if (q instanceof AutomatonQuery) {
      final AutomatonQuery aq = (AutomatonQuery) q;
      setString(n, "text", getString(n, "text") + ", " + aq.toString());
      // get automaton
      try {
        final java.lang.reflect.Field aField = AutomatonQuery.class.getDeclaredField("automaton");
        aField.setAccessible(true);
        final Automaton a = (Automaton) aField.get(aq);
        addAutomaton(n, a);
      }
      catch (final Exception e) {
        e.printStackTrace();
        final Object n1 = create("node");
        setString(n1, "text", "Automaton: Exception " + e.getMessage());
        add(n, n1);
      }
    }
    else if (q instanceof MultiTermQuery) {
      final MultiTermQuery mq = (MultiTermQuery) q;
      final Set<Term> terms = new HashSet<Term>();
      mq.extractTerms(terms);
      setString(n, "text", getString(n, "text") + ", terms: " + terms);
      try {
        addTermsEnum(n, TermRangeQuery.class, mq.getField(), mq);
      }
      catch (final Exception e) {
        e.printStackTrace();
        final Object n1 = create("node");
        setString(n1, "text", "TermEnum: Exception " + e.getMessage());
        add(n, n1);
      }
    }
    else if (q instanceof ConstantScoreQuery) {
      final ConstantScoreQuery cq = (ConstantScoreQuery) q;
      setString(n, "text", getString(n, "text") + ", " + cq.toString());
      final Object n1 = create("node");
      add(n, n1);
      if (cq.getFilter() != null) {
        setString(n1, "text", "Filter: " + cq.getFilter().toString());
      }
      else if (cq.getQuery() != null) {
        _explainStructure(n, cq.getQuery());
      }
    }
    else if (q instanceof FilteredQuery) {
      final FilteredQuery fq = (FilteredQuery) q;
      final Object n1 = create("node");
      setString(n1, "text", "Filter: " + fq.getFilter().toString());
      add(n, n1);
      _explainStructure(n, fq.getQuery());
    }
    else if (q instanceof SpanQuery) {
      final SpanQuery sq = (SpanQuery) q;
      final Class sqlass = sq.getClass();
      setString(n, "text", getString(n, "text") + ", field=" + sq.getField());
      if (sqlass == SpanOrQuery.class) {
        final SpanOrQuery soq = (SpanOrQuery) sq;
        setString(n, "text", getString(n, "text") + ", " + soq.getClauses().length + " clauses");
        for (final SpanQuery sq1 : soq.getClauses()) {
          _explainStructure(n, sq1);
        }
      }
      else if (sqlass == SpanFirstQuery.class) {
        final SpanFirstQuery sfq = (SpanFirstQuery) sq;
        setString(n, "text", getString(n, "text") + ", end=" + sfq.getEnd() + ", match:");
        _explainStructure(n, sfq.getMatch());
      }
      else if (q instanceof SpanNearQuery) { // catch also known subclasses
        final SpanNearQuery snq = (SpanNearQuery) sq;
        setString(n, "text", getString(n, "text") + ", slop=" + snq.getSlop());
        if (snq instanceof PayloadNearQuery) {
          try {
            final java.lang.reflect.Field function =
              PayloadNearQuery.class.getDeclaredField("function");
            function.setAccessible(true);
            final Object func = function.get(snq);
            setString(n, "text", getString(n, "text") + ", func=" + func.getClass().getSimpleName());
          }
          catch (final Exception e) {
            e.printStackTrace();
          }
        }
        for (final SpanQuery sq1 : snq.getClauses()) {
          _explainStructure(n, sq1);
        }
      }
      else if (sqlass == SpanNotQuery.class) {
        final SpanNotQuery snq = (SpanNotQuery) sq;
        Object n1 = create("node");
        add(n, n1);
        setString(n1, "text", "Include:");
        _explainStructure(n1, snq.getInclude());
        n1 = create("node");
        add(n, n1);
        setString(n1, "text", "Exclude:");
        _explainStructure(n1, snq.getExclude());
      }
      else if (q instanceof SpanTermQuery) {
        final SpanTermQuery stq = (SpanTermQuery) sq;
        setString(n, "text", getString(n, "text") + ", term=" + stq.getTerm());
        if (stq instanceof PayloadTermQuery) {
          try {
            final java.lang.reflect.Field function =
              PayloadTermQuery.class.getDeclaredField("function");
            function.setAccessible(true);
            final Object func = function.get(stq);
            setString(n, "text", getString(n, "text") + ", func=" + func.getClass().getSimpleName());
          }
          catch (final Exception e) {
            e.printStackTrace();
          }
        }
      }
      else {
        final String defField = getDefaultField(find("srchOptTabs"));
        setString(n, "text", "class=" + q.getClass().getName() + ", " + getString(n, "text")
          + ", toString=" + q.toString(defField));
        final HashSet<Term> terms = new HashSet<Term>();
        sq.extractTerms(terms);
        Object n1 = null;
        if (terms != null) {
          n1 = create("node");
          setString(n1, "text", "Matched terms (" + terms.size() + "):");
          add(n, n1);
          final Iterator<Term> it = terms.iterator();
          while (it.hasNext()) {
            final Object n2 = create("node");
            final Term t = it.next();
            setString(n2, "text", "field='" + t.field() + "' text='" + t.text() + "'");
            add(n1, n2);
          }
        }
        else {
          n1 = create("node");
          setString(n1, "text", "<no terms matched>");
          add(n, n1);
        }
      }
      if (ir != null) {
        final Object n1 = null;
        /*
         * in Lucene 4.0 this requires traversal of sub- and leaf readers, which is cumbersome to do here. try { Spans
         * spans = sq.getSpans(ir); if (spans != null) { n1 = create("node"); int cnt = 0; while (spans.next()) { Object
         * n2 = create("node"); setString(n2, "text", "doc=" + spans.doc() + ", start=" + spans.start() + ", end=" +
         * spans.end()); add(n1, n2); cnt++; } if (cnt > 0) { add(n, n1); setString(n1, "text", "Spans (" + cnt + "):");
         * setBoolean(n1, "expanded", false); } } } catch (Exception e) { e.printStackTrace(); n1 = create("node");
         * setString(n1, "text", "Spans Exception: " + e.getMessage()); add(n, n1); }
         */
      }
    }
    else {
      Object n1 = create("node");
      final String defField = getDefaultField(find("srchOptTabs"));
      final Set<Term> terms = new HashSet<Term>();
      q.extractTerms(terms);
      setString(n1, "text", q.getClass().getName() + ": " + q.toString(defField));
      add(n, n1);
      if (!terms.isEmpty()) {
        n1 = create("node");
        setString(n1, "text", "terms: " + terms);
        add(n, n1);
      }
    }
  }

  private void addAutomaton(final Object parent, final Automaton a) {
    final Object n = create("node");
    setString(n, "text", "Automaton: " + a != null ? a.toDot() : "null");
    add(parent, n);
    final State[] states = a.getNumberedStates();
    for (final State s : states) {
      final Object n1 = create("node");
      add(n, n1);
      final StringBuilder msg = new StringBuilder();
      msg.append(String.valueOf(s.getNumber()));
      if (a.getInitialState() == s) {
        msg.append(" INITIAL");
      }
      msg.append(s.isAccept() ? " [accept]" : " [reject]");
      msg.append(", " + s.numTransitions + " transitions");
      setString(n1, "text", msg.toString());
      for (final Transition t : s.getTransitions()) {
        final Object n2 = create("node");
        add(n1, n2);
        setString(n2, "text", t.toString());
      }
    }
  }

  private void addTermsEnum(final Object parent, final Class<? extends Query> clz,
    final String field, final Query instance) throws Exception {
    final Method m = clz.getDeclaredMethod("getTermsEnum", Terms.class, AttributeSource.class);
    m.setAccessible(true);
    final Terms terms = ar.terms(field);
    final TermsEnum fte = (TermsEnum) m.invoke(instance, terms, new AttributeSource());
    final Object n1 = create("node");
    final String clazz = fte.getClass().getName();
    setString(n1, "text", clazz);
    add(parent, n1);
    while (fte.next() != null) {
      final Object n2 = create("node");
      setString(n2, "text", "'" + fte.term().utf8ToString() + "', docFreq=" + fte.docFreq()
        + ", totalTermFreq=" + fte.totalTermFreq());
      add(n1, n2);
    }

  }

  public void clipQExplain(final Object qExplain) {
    final Object tree = find(qExplain, "qTree");
    final StringBuilder sb = new StringBuilder();
    treeToString(tree, sb);
    final StringSelection sel = new StringSelection(sb.toString());
    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel, this);
  }

  private void treeToString(final Object tree, final StringBuilder sb) {
    if (tree == null) {
      return;
    }
    final Object[] items = getItems(tree);
    if (items == null || items.length == 0) {
      return;
    }
    for (int i = 0; i < items.length; i++) {
      if (i > 0) {
        sb.append('\n');
      }
      treeNodeToString(items[i], sb, 0);
    }
  }

  private void treeNodeToString(final Object node, final StringBuilder sb, final int level) {
    for (int i = 0; i < level; i++) {
      sb.append(' ');
    }
    sb.append(getString(node, "text"));
    final Object[] items = getItems(node);
    if (items != null && items.length > 0) {
      sb.append('\n');
      for (int i = 0; i < items.length; i++) {
        if (i > 0) {
          sb.append('\n');
        }
        treeNodeToString(items[i], sb, level + 2);
      }
    }
  }

  /**
   * Update the parsed and rewritten query views.
   */
  public void showParsed() {
    final Object qField = find("qField");
    final Object qFieldParsed = find("qFieldParsed");
    final Object qFieldRewritten = find("qFieldRewritten");
    final String queryS = getString(qField, "text");
    if (queryS.trim().equals("")) {
      setString(qFieldParsed, "text", "<empty query>");
      setBoolean(qFieldParsed, "enabled", false);
      return;
    }
    else {
      setBoolean(qFieldParsed, "enabled", true);
    }
    try {
      Query q = createQuery(queryS);
      if (q == null) {
        return;
      }
      setString(qFieldParsed, "text", q.toString());
      putProperty(qField, "qParsed", q);
      q = q.rewrite(ir);
      setString(qFieldRewritten, "text", q.toString());
      putProperty(qField, "qRewritten", q);
    }
    catch (final Throwable t) {
      t.printStackTrace();
      setString(qFieldParsed, "text", t.getMessage());
      setString(qFieldRewritten, "text", t.getMessage());
    }
  }

  /**
   * Perform a search. NOTE: this method is usually invoked from the GUI.
   *
   * @param qField Thinlet widget containing the query
   */
  public void search(final Object qField) {
    if (ir == null) {
      showStatus(MSG_NOINDEX);
      return;
    }
    if (ir.numDocs() == 0) {
      showStatus(MSG_EMPTY_INDEX);
      return;
    }
    final String queryS = getString(qField, "text");
    if (queryS.trim().equals("")) {
      showStatus("FAILED: Empty query.");
      return;
    }
    final Object srchOpts = find("srchOptTabs");
    // query parser opts
    final Similarity sim = createSimilarity(srchOpts);
    AccessibleHitCollector col;
    try {
      col = createCollector(srchOpts);
    }
    catch (final Throwable t) {
      errorMsg("ERROR creating Collector: " + t.getMessage());
      return;
    }
    final Object sTable = find("sTable");
    final Object cntRepeat = find("cntRepeat");
    final int repeat = Integer.parseInt(getString(cntRepeat, "text"));
    removeAll(sTable);
    Query q = null;
    try {
      q = createQuery(queryS);
      if (q == null) {
        return;
      }
      is.setSimilarity(sim);
      showParsed();
      _search(q, is, col, sTable, repeat);
    }
    catch (final Throwable e) {
      e.printStackTrace();
      errorMsg(e.getMessage());
    }
  }

  int resStart = 0;
  int resCount = 20;
  LimitedException le = null;

  private void _search(final Query q, final IndexSearcher is, AccessibleHitCollector hc,
    final Object sTable, final int repeat) throws Exception {
    if (hc == null) {
      hc = new AccessibleTopHitCollector(1000, true, true);
    }
    final AccessibleHitCollector collector = hc;
    le = null;
    final SlowThread t = new SlowThread(this) {

      @Override
      public void execute() {
        final long startTime = System.nanoTime();
        for (int i = 0; i < repeat; i++) {
          if (i > 0) {
            collector.reset();
          }
          try {
            is.search(q, collector);
          }
          catch (final LimitedException e) {
            le = e;
          }
          catch (final Throwable th) {
            th.printStackTrace();
            errorMsg("ERROR searching: " + th.toString());
            return;
          }
        }
        final long endTime = System.nanoTime();
        final long delta = (endTime - startTime) / 1000 / repeat;
        String msg;
        if (delta > 100000) {
          msg = delta / 1000 + " ms";
        }
        else {
          msg = delta + " us";
        }
        if (repeat > 1) {
          msg += " (avg of " + repeat + " runs)";
        }
        showSearchStatus(msg);
        final Object bsPrev = find("bsPrev");
        final Object bsNext = find("bsNext");
        setBoolean(bsNext, "enabled", false);
        setBoolean(bsPrev, "enabled", false);
        final int resNum = collector.getTotalHits();
        if (resNum == 0) {
          final Object row = create("row");
          Object cell = create("cell");
          add(sTable, row);
          add(row, cell);
          cell = create("cell");
          add(row, cell);
          cell = create("cell");
          setString(cell, "text", "No Results");
          setBoolean(cell, "enabled", false);
          add(row, cell);
          setString(find("resNum"), "text", "0");
          return;
        }

        if (resNum > resCount) {
          setBoolean(bsNext, "enabled", true);
        }
        setString(find("resNum"), "text", String.valueOf(resNum));
        putProperty(sTable, "resNum", new Integer(resNum));
        putProperty(sTable, "query", q);
        putProperty(sTable, "hc", collector);
        if (le != null) {
          putProperty(sTable, "le", le);
        }
        resStart = 0;
        _showSearchPage(sTable);
      }
    };
    if (slowAccess) {
      t.start();
    }
    else {
      t.execute();
    }
  }

  public void prevPage(final Object sTable) {
    final int resNum = ((Integer) getProperty(sTable, "resNum")).intValue();
    if (resStart == 0) {
      setBoolean(find("bsPrev"), "enabled", false);
      return;
    }
    resStart -= resCount;
    if (resStart < 0) {
      resStart = 0;
    }
    if (resStart - resCount < 0) {
      setBoolean(find("bsPrev"), "enabled", false);
    }
    if (resStart + resCount < resNum) {
      setBoolean(find("bsNext"), "enabled", true);
    }
    _showSearchPage(sTable);
  }

  public void nextPage(final Object sTable) {
    final int resNum = ((Integer) getProperty(sTable, "resNum")).intValue();
    resStart += resCount;
    if (resStart >= resNum) {
      resStart -= resCount;
      setBoolean(find("bsNext"), "enabled", false);
      return;
    }
    setBoolean(find("bsPrev"), "enabled", true);
    if (resStart + resCount >= resNum) {
      setBoolean(find("bsNext"), "enabled", false);
    }
    _showSearchPage(sTable);
  }

  private void _showSearchPage(final Object sTable) {
    final SlowThread t = new SlowThread(this) {

      @Override
      public void execute() {
        try {
          removeAll(sTable);
          final AccessibleHitCollector hc = (AccessibleHitCollector) getProperty(sTable, "hc");
          final int resNum = hc.getTotalHits();
          final int max = Math.min(resNum, resStart + resCount);
          final Object posLabel = find("resPos");
          setString(posLabel, "text", resStart + "-" + (max - 1));
          for (int i = resStart; i < max; i++) {
            final int docid = hc.getDocId(i);
            final float score = hc.getScore(i);
            _createResultRow(i, docid, score, sTable);
          }
        }
        catch (final Exception e) {
          e.printStackTrace();
          showStatus(e.getMessage());
        }
      }
    };
    if (slowAccess) {
      t.start();
    }
    else {
      t.execute();
    }
  }

  private void _createResultRow(final int pos, final int docId, final float score,
    final Object sTable) throws IOException {
    final Object row = create("row");
    Object cell = create("cell");
    add(sTable, row);
    setString(cell, "text", String.valueOf(pos));
    setChoice(cell, "alignment", "right");
    add(row, cell);
    cell = create("cell");
    setString(cell, "text", String.valueOf(df.format(score)));
    setChoice(cell, "alignment", "right");
    add(row, cell);
    cell = create("cell");
    setString(cell, "text", String.valueOf(docId));
    setChoice(cell, "alignment", "right");
    add(row, cell);
    final Document doc = ir.document(docId);
    putProperty(row, "docid", new Integer(docId));
    final StringBuffer vals = new StringBuffer();
    for (int j = 0; j < idxFields.length; j++) {
      cell = create("cell");
      Decoder dec = decoders.get(idxFields[j]);
      if (dec == null) {
        dec = defDecoder;
      }
      final IndexableField[] values = doc.getFields(idxFields[j]);
      vals.setLength(0);
      boolean decodeErr = false;
      if (values != null) {
        for (int k = 0; k < values.length; k++) {
          if (k > 0) {
            vals.append(' ');
          }
          String v;
          try {
            v = dec.decodeStored(idxFields[j], (Field) values[k]);
          }
          catch (final Throwable e) {
            e.printStackTrace();
            v = values[k].stringValue();
            decodeErr = true;
          }
          vals.append(Util.escape(v));
        }
      }
      setString(cell, "text", vals.toString());
      if (decodeErr) {
        setColor(cell, "foreground", Color.RED);
      }
      add(row, cell);
    }
  }

  /**
   * Pop up a modal dialog explaining the selected result.
   *
   * @param sTable Thinlet table widget containing selected search result.
   */
  public void explainResult(final Object sTable) {
    final Object row = getSelectedItem(sTable);
    if (row == null) {
      return;
    }
    final Integer docid = (Integer) getProperty(row, "docid");
    if (docid == null) {
      return;
    }
    if (ir == null) {
      showStatus(MSG_NOINDEX);
      return;
    }
    final Query q = (Query) getProperty(sTable, "query");
    if (q == null) {
      return;
    }
    final Thread t = new Thread() {

      @Override
      public void run() {
        try {
          final IndexSearcher is = new IndexSearcher(ir);
          final Similarity sim = createSimilarity(find("srchOptTabs"));
          is.setSimilarity(sim);
          final Explanation expl = is.explain(q, docid.intValue());
          final Object dialog = addComponent(null, "/xml/explain.xml", null, null);
          final Object eTree = find(dialog, "eTree");
          addNode(eTree, expl);
          // setBoolean(eTree, "expand", true);
          add(dialog);
        }
        catch (final Exception e) {
          e.printStackTrace();
          errorMsg(e.getMessage());
        }
      }
    };
    if (slowAccess) {
      t.start();
    }
    else {
      t.run();
    }
  }

  public void clipExplain(final Object explain) {
    final Object eTree = find(explain, "eTree");
    final StringBuilder sb = new StringBuilder();
    treeToString(eTree, sb);
    final StringSelection sel = new StringSelection(sb.toString());
    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel, this);
  }

  private final DecimalFormat df = new DecimalFormat("0.0000");
  private String xmlQueryParserFactoryClassName = CorePlusExtensionsParserFactory.class.getName();

  private void addNode(final Object tree, final Explanation expl) {
    final Object node = create("node");
    setString(node, "text", df.format(expl.getValue()) + "  " + expl.getDescription());
    add(tree, node);
    if (getClass(tree) == "tree") {
      setFont(node, getFont().deriveFont(Font.BOLD));
    }
    final Explanation[] kids = expl.getDetails();
    if (kids != null && kids.length > 0) {
      for (int i = 0; i < kids.length; i++) {
        addNode(node, kids[i]);
      }
    }
  }

  public void gotoDoc(final Object sTable) {
    final Object row = getSelectedItem(sTable);
    if (row == null) {
      return;
    }
    final Integer docid = (Integer) getProperty(row, "docid");
    if (docid == null) {
      return;
    }
    if (ir == null) {
      showStatus(MSG_NOINDEX);
      return;
    }
    final SlowThread st = new SlowThread(this) {

      @Override
      public void execute() {
        Document doc = null;
        try {
          doc = ir.document(docid.intValue());
        }
        catch (final Exception e) {
          e.printStackTrace();
          showStatus(e.getMessage());
          return;
        }
        _showDocFields(docid.intValue(), doc);
        final Object tabpane = find("maintpane");
        setInteger(tabpane, "selected", 1);
        repaint();
      }
    };
    if (slowAccess) {
      st.start();
    }
    else {
      st.execute();
    }
  }

  private void _showTermDoc(final Object fText, final DocsEnum td) {
    if (ir == null) {
      showStatus(MSG_NOINDEX);
      return;
    }
    final SlowThread st = new SlowThread(this) {

      @Override
      public void execute() {
        try {
          final Document doc = ir.document(td.docID());
          setString(find("docNum"), "text", String.valueOf(td.docID()));
          setString(find("tFreq"), "text", String.valueOf(td.freq()));
          _showDocFields(td.docID(), doc);
        }
        catch (final Exception e) {
          e.printStackTrace();
          showStatus(e.getMessage());
        }
      }
    };
    if (slowAccess) {
      st.start();
    }
    else {
      st.execute();
    }
  }

  public void deleteTermDoc(final Object fText) {
    final Term t = (Term) getProperty(fText, "term");
    if (t == null) {
      return;
    }
    if (ir == null) {
      showStatus(MSG_NOINDEX);
      return;
    }
    if (readOnly) {
      showStatus(MSG_READONLY);
      return;
    }
    try {
      final IndexWriter iw = createIndexWriter();
      iw.deleteDocuments(t);
      iw.close();
      refreshAfterWrite();
      infoMsg("Deleted docs for query '"
        + t
        + "'. Term dictionary and statistics will be incorrect until next merge or expunge deletes.");
    }
    catch (final Exception e) {
      e.printStackTrace();
      errorMsg("Error deleting doc: " + e.toString());
    }
  }

  public void deleteDocs(final Object sTable) {
    if (ir == null) {
      showStatus(MSG_NOINDEX);
      return;
    }
    if (readOnly) {
      showStatus(MSG_READONLY);
      return;
    }
    final Query q = (Query) getProperty(sTable, "query");
    if (q == null) {
      errorMsg("Empty query.");
      return;
    }
    removeAll(sTable);
    try {
      final IndexWriter iw = createIndexWriter();
      iw.deleteDocuments(q);
      iw.close();
      refreshAfterWrite();
      infoMsg("Deleted docs for query '"
        + q
        + "'. Term dictionary and statistics will be incorrect until next merge or expunge deletes.");
    }
    catch (final Throwable e) {
      e.printStackTrace();
      errorMsg("Error deleting documents: " + e.toString());
    }
  }

  public void actionAbout() {
    final Object about = addComponent(this, "/xml/about.xml", null, null);
    final Object lver = find(about, "lver");
    setString(lver, "text", "Lucene version: " + LucenePackage.get().getImplementationVersion());
  }

  /**
   * Pop up a modal font selection dialog.
   */
  public void actionShowFonts() {
    addComponent(this, "/xml/selfont.xml", null, null);
  }

  /**
   * Initialize the font selection dialog.
   *
   * @param selfont font selection dialog
   */
  public void setupSelFont(final Object selfont) {
    final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    final Font[] fonts = ge.getAllFonts();
    final Object cbFonts = find(selfont, "fonts");
    final String curfont = getFont().getFontName();
    final float cursize = getFont().getSize2D();
    final Object fsize = find(selfont, "fsize");
    final NumberFormat nf = NumberFormat.getNumberInstance();
    nf.setMaximumFractionDigits(1);
    setString(fsize, "text", nf.format(cursize));
    removeAll(cbFonts);
    final Object def = create("choice");
    setFont(def, "font", getFont().deriveFont(15.0f));
    setString(def, "text", curfont + " (default)");
    putProperty(def, "fnt", getFont());
    add(cbFonts, def);
    setInteger(cbFonts, "selected", 0);
    for (int i = 0; i < fonts.length; i++) {
      final Object choice = create("choice");
      setFont(choice, "font", fonts[i].deriveFont(15.0f));
      setString(choice, "text", fonts[i].getFontName());
      putProperty(choice, "fnt", fonts[i]);
      add(cbFonts, choice);
      if (curfont.equalsIgnoreCase(fonts[i].getFontName())) {
        setInteger(cbFonts, "selected", i + 1);
      }
    }
  }

  /**
   * Show preview of the selected font.
   *
   * @param selfont font selection dialog
   */
  public void selectFont(final Object selfont) {
    final Object preview = find(selfont, "fpreview");
    final Object cbFonts = find(selfont, "fonts");
    final Object fsize = find(selfont, "fsize");
    Font f = (Font) getProperty(getSelectedItem(cbFonts), "fnt");
    float size = getFont().getSize2D();
    try {
      size = Float.parseFloat(getString(fsize, "text"));
    }
    catch (final Exception e) {
      e.printStackTrace();
    }
    f = f.deriveFont(size);
    final Object[] items = getItems(preview);
    for (int i = 0; i < items.length; i++) {
      setPreviewFont(f, items[i]);
    }
  }

  private void setPreviewFont(final Font f, final Object item) {
    try {
      setFont(item, "font", f);
    }
    catch (final IllegalArgumentException iae) {
      // shrug...
    }
    final Object[] items = getItems(item);
    for (int i = 0; i < items.length; i++) {
      setPreviewFont(f, items[i]);
    }
  }

  /**
   * Set the default font in the UI.
   *
   * @param selfont font selection dialog
   */
  public void actionSetFont(final Object selfont) {
    final Object cbFonts = find(selfont, "fonts");
    final Object fsize = find(selfont, "fsize");
    Font f = (Font) getProperty(getSelectedItem(cbFonts), "fnt");
    float size = getFont().getSize2D();
    try {
      size = Float.parseFloat(getString(fsize, "text"));
    }
    catch (final Exception e) {
      e.printStackTrace();
    }
    f = f.deriveFont(size);
    remove(selfont);
    setFont(f);
    courier = new Font("Courier", getFont().getStyle(), getFont().getSize());
    repaint();
  }

  public void actionSetDecoder(final Object fList, final Object combo) {
    final Object row = getSelectedItem(fList);
    if (row == null) {
      return;
    }
    final String fName = (String) getProperty(row, "fName");
    final Object choice = getSelectedItem(combo);
    final String decName = getString(choice, "name");
    Decoder dec = null;
    if (decName.equals("s")) {
      dec = new StringDecoder();
    }
    else if (decName.equals("b")) {
      dec = new BinaryDecoder();
    }
    else if (decName.equals("d")) {
      dec = new DateDecoder();
    }
    else if (decName.equals("nl")) {
      dec = new NumLongDecoder();
    }
    else if (decName.equals("ni")) {
      dec = new NumIntDecoder();
    }
    else if (decName.startsWith("solr.")) {
      try {
        dec = new SolrDecoder(decName);
      }
      catch (final Exception e) {
        e.printStackTrace();
        errorMsg("Error setting decoder: " + e.toString());
      }
    }
    else {
      dec = defDecoder;
    }
    decoders.put(fName, dec);
    final Object cell = getItem(row, 3);
    setString(cell, "text", dec.toString());
    repaint(fList);
    actionTopTerms(find("nTerms"));
  }

  /**
   * Returns current custom similarity implementation.
   *
   * @return
   */
  public Similarity getCustomSimilarity() {
    return similarity;
  }

  private static TFIDFSimilarity defaultSimilarity = new DefaultSimilarity();

  /**
   * Set the current custom similarity implementation.
   *
   * @param s
   */
  public void setCustomSimilarity(final Similarity s) {
    similarity = s;
    final Object cbSimCust = find("ckSimCust");
    final Object cbSimDef = find("ckSimDef");
    final Object simName = find("simName");
    if (similarity != null) {
      setString(simName, "text", similarity.getClass().getName());
      setBoolean(cbSimCust, "enabled", true);
    }
    else {
      setString(simName, "text", "");
      setBoolean(cbSimCust, "enabled", false);
      setBoolean(cbSimDef, "selected", true);
      setBoolean(cbSimCust, "selected", false);
    }
  }

  /**
   * Switch the view to display the SimilarityDesigner plugin, if present.
   */
  public void actionDesignSimilarity() {
    LukePlugin designer = null;
    for (int i = 0; i < plugins.size(); i++) {
      if (plugins.get(i).getClass().getName()
        .equals("org.getopt.luke.plugins.SimilarityDesignerPlugin")) {
        designer = plugins.get(i);
        break;
      }
    }
    if (designer == null) {
      showStatus("Designer Plugin not available");
      return;
    }
    // a bit tricky: plugins are put within panels, and these in tabs
    final Object pluginsTab = find("pluginsTab");
    final Object maintab = getParent(pluginsTab);
    int index = getIndex(maintab, pluginsTab);
    setInteger(maintab, "selected", index);
    final Object pluginsTabs = find("pluginsTabs");
    final Object tab = getParent(getParent(designer.getMyUi()));
    index = getIndex(pluginsTabs, tab);
    setInteger(pluginsTabs, "selected", index);
    repaint();
  }

  /**
   * Shut down Luke. If {@link #exitOnDestroy} is true (such as when Luke was started from the main method), invoke also
   * System.exit().
   */
  @Override
  public boolean destroy() {
    if (ir != null) {
      try {
        ir.close();
      }
      catch (final Exception e) {
      }
    }
    ;
    if (ar != null) {
      try {
        ar.close();
      }
      catch (final Exception e) {
      }
    }
    ;
    if (dir != null) {
      try {
        dir.close();
      }
      catch (final Exception e) {
      }
    }
    ;
    try {
      Prefs.save();
    }
    catch (final Exception e) {
    }
    ;
    if (exitOnDestroy) {
      System.exit(0);
    }
    return super.destroy();
  }

  public void actionExit() {
    destroy();
  }

  /**
   * Open URL in the system default browser.
   *
   * @param url
   */
  public void goUrl(final Object url) {
    final String u = (String) getProperty(url, "url");
    if (u == null) {
      return;
    }
    try {
      BrowserLauncher.openURL(u);
    }
    catch (final Exception e) {
      e.printStackTrace();
      showStatus(e.getMessage());
    }
  }

  /**
   * Start the GUI, and optionally open an index.
   *
   * @param args index parameters
 * @param metadata
   * @return fully initialized Leia instance
   */
  public static Leia start(final String[] args, ApplicationMetaData metadata) {
    final Leia leia = new Leia();
    final FrameLauncher f =
      new FrameLauncher(metadata.toString(), leia, 850, 650);
    f.setIconImage(Toolkit.getDefaultToolkit().createImage(Leia.class.getResource("/img/luke.gif")));
    if (args.length > 0) {
      boolean force = false, ro = false, ramdir = false;
      String pName = null;
      String script = null;
      String xmlQueryParserFactoryClassName = null;
      for (int i = 0; i < args.length; i++) {
        if (args[i].equalsIgnoreCase("-ro")) {
          ro = true;
        }
        else if (args[i].equalsIgnoreCase("-force")) {
          force = true;
        }
        else if (args[i].equalsIgnoreCase("-ramdir")) {
          ramdir = true;
        }
        else if (args[i].equalsIgnoreCase("-index")) {
          pName = args[++i];
        }
        else if (args[i].equalsIgnoreCase("-script")) {
          script = args[++i];
        }
        else if (args[i].equalsIgnoreCase("-xmlQueryParserFactory")) {
          xmlQueryParserFactoryClassName = args[++i];
        }
        else {
          System.err.println("Unknown argument: " + args[i]);
          usage();
          leia.actionExit();
          return null;
        }
      }
      if (pName != null) {
        leia.openIndex(pName, force, null, ro, ramdir, false, null, 1);
      }
      if (xmlQueryParserFactoryClassName != null) {
        leia.setParserFactoryClassName(xmlQueryParserFactoryClassName);
      }
      if (script != null) {
        final LukePlugin plugin = leia.getPlugin("org.getopt.luke.plugins.ScriptingPlugin");
        if (plugin == null) {
          final String msg = "ScriptingPlugin not present - cannot execute scripts.";
          System.err.println(msg);
          leia.actionExit();
        }
        else {
          ((ScriptingPlugin) plugin).execute("load('" + script + "');");
        }
      }
    }
    else {
      leia.actionOpen();
    }
    return leia;
  }

  private void setParserFactoryClassName(final String xmlQueryParserFactoryClassName) {
    this.xmlQueryParserFactoryClassName = xmlQueryParserFactoryClassName;
  }

  /**
   * Main method. If you just want to instantiate Luke from other classes or scripts, use {@link #startLuke(String[])}
   * instead.
   *
   * @param args
 * @throws Exception
   */
  public static void main(final String[] args) throws Exception {
    exitOnDestroy = true;
    InputStream manifestInpuStream = Leia.class.getResourceAsStream("/META-INF/MANIFEST.MF");
    Manifest manifest = new Manifest(manifestInpuStream);
    ApplicationMetaData metadata = new ApplicationMetaData(manifest.getMainAttributes(), manifest.getEntries());
    start(args,metadata);
  }

  public static void usage() {
    System.err.println("Command-line usage:\n");
    System.err.println("Leia [-index path_to_index] [-ro] [-force] [-mmap] [-script filename]\n");
    System.err.println("\t-index path_to_index\topen this index");
    System.err.println("\t-ro\topen index read-only");
    System.err.println("\t-force\tforce unlock if the index is locked (use with caution)");
    System.err
    .println("\t-xmlQueryParserFactory\tFactory for loading custom XMLQueryParsers. E.g.:");
    System.err.println("\t\t\torg.getopt.luke.xmlQuery.CoreParserFactory (default)");
    System.err.println("\t\t\torg.getopt.luke.xmlQuery.CorePlusExtensionsParserFactory");
    System.err.println("\t-mmap\tuse MMapDirectory");
    System.err.println("\t-script filename\trun this script using the ScriptingPlugin.");
    System.err.println("\t\tIf an index name is specified, the index is open prior to");
    System.err.println("\t\tstarting the script. Note that you need to escape special");
    System.err.println("\t\tcharacters twice - first for shell and then for JavaScript.");

  }

  /*
   * (non-Javadoc)
   * @see java.awt.datatransfer.ClipboardOwner#lostOwnership(java.awt.datatransfer.Clipboard,
   * java.awt.datatransfer.Transferable)
   */
  @Override
  public void lostOwnership(final Clipboard arg0, final Transferable arg1) {

  }

  /**
   * @return the numTerms
   */
  public int getNumTerms() {
    return numTerms;
  }

}
