package org.javautil.oracle.trace;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.javautil.oracle.trace.record.CursorRecord;
import org.javautil.oracle.trace.record.Parsing;
import org.javautil.oracle.trace.record.RecordType;
import org.javautil.util.EventHelper;

public class Cursors {
	private static EventHelper events = new EventHelper();

	private static final Integer SHOW_DEPTH = 1;

	private static final Integer LOG_ALL_CURSORS_SIZE = 2;

	// private static final Integer LOG_GROUP_COUNT = 4;

	private static final Integer DUMP_EACH_CURSOR_ON_MATCH = 8;

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	private final HashMap<String, ArrayList<Cursor>> bySqlId = new HashMap<String, ArrayList<Cursor>>();

	/**
	 * Keyed by cursor number.
	 * 
	 * The cursor number
	 * 
	 * During the course of a trace file a cursor number may be reused. Once a
	 * parsing record is found for a cursor number any prior references to that
	 * cursor number do not apply to the new cursor.
	 * 
	 * The maximum cursor number should be equal to the number of open cursors.
	 */
	private final HashMap<Integer, Cursor> byCursorNumber = new HashMap<Integer, Cursor>();

	/**
	 * A surrogate key for cursors.
	 * 
	 * Assigned with every new parse.
	 */
	private int nextCursorId = 1;

	/**
	 * TODO document
	 * 
	 */
	private final HashMap<Integer, Cursor> depthMap = new HashMap<Integer, Cursor>();

	// private ArrayList<AggregateCursor> aggregates;

	private final HashMap<Integer, ArrayList<Cursor>> orphans = new HashMap<Integer, ArrayList<Cursor>>();
	/**
	 * Every cursor encountered.
	 */
	private final ArrayList<Cursor> cursors = new ArrayList<Cursor>();

	/*
	 *
	 *
	 */
	// private ArrayList<Cursor> allCursors = new ArrayList<Cursor>();
	/**
	 * Cursors by key. The key is defined by the address and hash of the cursor.
	 * The hash is a hash of the sqlText as computed by Oracle. The address is
	 * the address of the cursor in the ???
	 * 
	 * @todo PGA/UGA/SGA
	 */
	// private HashMap<String, ArrayList<Cursor>> byKey = new HashMap<String,
	// ArrayList<Cursor>>();

	// private final HashMap<String, AggregateCursor> aggByKey = new
	// HashMap<String, AggregateCursor>();

	// private TreeMap<Integer,Cursor> byId = new TreeMap<Integer,Cursor>();

	// private ArrayList<Cursor> unknown;

	/**
	 * @todo implement
	 */
	// private boolean filterOutLogicalIdZeroCursors = false;

	private int previousDepth = 0;

	static {
		events.addEvent(LOG_ALL_CURSORS_SIZE);
		events.addEvent(DUMP_EACH_CURSOR_ON_MATCH);
	}

	// public Collection<AggregateCursor> getAggregateCursorsById() {
	// return aggByKey.values();
	// }

	// private HashMap<String,AggregateCursor> aggByKey = new
	// HashMap<String,AggregateCursor>();
	/**
	 * We got here because we are now at a lower depth than the preceding Event.
	 * 
	 * @todo flesh this out.
	 * @param rec
	 */
	// private void resolveOrphans(Cursor rec) {
	// if (rec == null) {
	// throw new IllegalArgumentException("rec is null");
	// }
	// int depth = rec.getParsing().getDepth();
	// int childDepth = depth + 1;
	// ArrayList<Cursor> myOrphans = orphans.get(childDepth);
	// rec.addChildren(myOrphans);
	// // for (Cursor c: orphans.get(childDepth)) {
	// // rec.addC
	// // }
	// }

	/**
	 * Collection of cursors sharing a common HashValue and Address.
	 * 
	 * For each distinct HashValue and address, create a collection of the
	 * cursors that have that value.
	 * 
	 * @return
	 */
	// public Map<String, ArrayList<Cursor>> group() {
	//
	// unknown = new ArrayList<Cursor>();
	//
	// if (events.exists(LOG_ALL_CURSORS_SIZE)) {
	// logger.info("allCursors.size() " + allCursors.size());
	// }
	// for (Cursor cursor : allCursors) {
	// String key = cursor.getKey();
	// if (key != null) {
	// // .info("matching on key " + key);
	// ArrayList<Cursor> matches = byKey.get(key);
	// if (matches == null) {
	// matches = new ArrayList<Cursor>();
	// byKey.put(key, matches);
	// }
	// matches.add(cursor);
	// if (events.exists(DUMP_EACH_CURSOR_ON_MATCH)) {
	// logger.info("Adding:\n" + cursor.toString());
	// }
	// } else {
	// // logger.info("null key");
	// unknown.add(cursor);
	// }
	// }
	// if (events.exists(LOG_GROUP_COUNT)) {
	// logger.info("cursors in " + allCursors.size() + " group count " +
	// byKey.size() + " unknowns " + unknown.size());
	// }
	// // logger.setLevel(oldLevel);
	// return byKey;
	// }

	// TODO Create XML Formatter

	// public Document getAggregatesDocument() {
	// final Document ad = DocumentHelper.createDocument();
	// final Element root = ad.addElement("aggregates");
	// // ArrayList<AggregateCursor> aggs = getAggregates();
	// for (final AggregateCursor cursor : aggByKey.values()) {
	// final CursorXmlFormatter cxf = new CursorXmlFormatter(cursor);
	// final Element el = cxf.asElement();
	// root.add(el);
	// }
	// return ad;
	// }

	//
	// public void dumpAggregates() {
	// ArrayList<AggregateCursor> aggs = getAggregates();
	// for (AggregateCursor cursor : aggs) {
	// CursorXmlFormatter cxf = new CursorXmlFormatter(cursor);
	// Element el = cxf.asElement();
	// System.out.println(el.asXML());
	// }
	// }
	//
	// public void verboseAggregateDump() {
	// if (aggregates == null) {
	// getAggregates();
	// }
	// for (AggregateCursor cursor : aggregates) {
	// System.out.println(cursor.toString());
	// }
	// }

	public int getNextCursorId() {
		return nextCursorId++;
	}

	// public void dumpGroups() {
	// if (byKey.size() == 0) {
	// group();
	// }
	// for (ArrayList<AggregateCursor> cursorGroup : byKey.values()) {
	// for (AggregateCursor cursor : cursorGroup) {
	// CursorXmlFormatter cxf = new CursorXmlFormatter(cursor);
	// Element el = cxf.asElement();
	// System.out.println(el.asXML());
	// }
	// }
	// for (ArrayList<AggregateCursor> cursorGroup : byKey.values()) {
	// for (AggregateCursor cursor : cursorGroup) {
	// CursorXmlFormatter cxf = new CursorXmlFormatter(cursor);
	// Element el = cxf.asElement();
	// System.out.println(el.asXML());
	// }
	// }
	// }

	/**
	 * Compute aggregates
	 * 
	 * @todo appears to aggregate direct children, but no way to get full
	 *       recursion to end point
	 * @return
	 */
	// public ArrayList<AggregateCursor> getAggregates() {
	// // if (byKey.size() == 0) {
	// // group();
	// // }
	// aggregates = new ArrayList<AggregateCursor>();
	//
	// for (String key : byKey.keySet()) {
	// // cursors with a common hash and address
	// ArrayList<Cursor> grp = byKey.get(key);
	//
	// Cursor first = grp.get(0);
	// Parsing p = first.getParsing();
	// AggregateCursor agg = new AggregateCursor(first, getNextCursorId());
	// agg.setParsing(p);
	// logger.info("created aggregate cursor " + agg.getKey() + " for " +
	// p.getSqlText());
	// aggregates.add(agg);
	// for (Cursor c : grp) {
	// agg.addCursor(c);
	// Collection<Cursor> children = c.getChildrenByKey();
	// agg.addChildren(children);
	//
	// for (Cursor child : children) {
	// agg.aggregate(child);
	// }
	// }
	// logger.info(agg.toString());
	// }
	// return aggregates;
	// }

	public void showDepth(final int toDepth) {
		final int showLength = 40;
		final StringBuilder b = new StringBuilder();
		b.append("====\n");
		b.append("to depth " + toDepth + "\n");

		for (int d = 0; d <= toDepth; d++) {
			b.append("depthMap.size() " + depthMap.size() + "\n");
			b.append("depth " + d + " ");
			final Cursor c = depthMap.get(d);
			if (c == null) {
				logger.error("no cursor at depth " + d);
				break;
			}
			final Parsing p = c.getParsing();
			if (p == null) {
				b.append("parsing is null \n");
			} else {
				final int len = showLength < p.getSqlText().length() ? showLength : p.getSqlText().length();
				b.append("parsing line # " + p.getLineNumber() + " " + p.getSqlText().substring(0, len) + "\n");
			}
		}
		logger.info(b.toString());
	}

	/**
	 * @todo DOM may get too big, might have to write elements to file by
	 *       converting child elements to xml fragments ad emitting
	 * @param fileName
	 * @throws IOException
	 */
	// public void writeAggregatesDocument(final String fileName)
	// throws IOException {
	// // if (fileName == null) {
	// // throw new IllegalArgumentException("fileName is null");
	// // }
	// // File f = new File(fileName);
	// //
	// // FileWriter fw = new FileWriter(fileName);
	// // OutputFormat outformat = OutputFormat.createPrettyPrint();
	// //
	// // XMLWriter writer = new XMLWriter(fw, outformat);
	// // Document d = getAggregatesDocument();
	// // writer.write(d);
	// // writer.flush();
	// // writer.close();
	//
	// }

	private String getLineMessage(final int lineNumber, final int cursorNumber, final String text) {
		return "line " + lineNumber + " cursor# " + cursorNumber + " " + text;
	}

	private Cursor processParsingRec(final Parsing p) {
		final Cursor c = new Cursor(p, getNextCursorId());
		if (logger.isDebugEnabled()) {
			logger.debug(c.getParsing());
		}
		cursors.add(c);
		if (logger.isDebugEnabled()) {
			logger.debug("at line " + p.getLineNumber() + " depth is " + p.getDepth());
		}
		processDepth(p, c);
		addByCursorNumber(c);

		addBySqlId(c);
		// allCursors.add(c);

		return c;
	}

	/**
	 * 
	 * @param cursor
	 */
	void addByCursorNumber(Cursor cursor) {
		byCursorNumber.put(cursor.getCursorNumber(), cursor);
	}

	private void processDepth(Parsing p, Cursor c) {
		final int depth = p.getDepth();
		depthMap.put(depth, c);
		if (events.exists(SHOW_DEPTH)) {
			showDepth(depth);
		}
		if (depth > 0) {
			ArrayList<Cursor> orphanList = orphans.get(depth);
			if (orphanList == null) {
				orphanList = new ArrayList<Cursor>();
			}
			orphanList.add(c);
			orphans.put(depth, orphanList);

		}
		// TODO this is all backwards
		if (depth < previousDepth) {
			c.addChildren(orphans.get(depth + 1));
			orphans.put(depth + 1, new ArrayList<Cursor>());
		}
		previousDepth = p.getDepth();
	}

	/**
	 * Add this cursor by SqlId
	 * 
	 * @param cursor
	 */
	void addBySqlId(final Cursor cursor) {
	}

	Cursor getCursor(final CursorRecord rec) {
		Cursor cursor = null;
		if (rec.getRecordType().equals(RecordType.PARSING)) {
			cursor = processParsingRec((Parsing) rec);
		} else {
			cursor = byCursorNumber.get(rec.getCursorNumber());
			if (cursor == null) {
				cursor = new Cursor(getNextCursorId());
				cursor.setCursorNumber(rec.getCursorNumber());
				byCursorNumber.put(cursor.getCursorNumber(), cursor);
				// allCursors.add(c);
			}
		}

		return cursor;
	}

	/**
	 * Creates or locates the cursor associated with this event and adds this
	 * event to the Cursor.
	 * 
	 * If the RecordType is PARSING, any previous cursor with that number is
	 * removed from open, a new one is created and added in its place.
	 */
	Cursor processCursorRecord(final CursorRecord rec) {
		// rec.getCursorNumber();
		Cursor cursor = getCursor(rec);

		if (!rec.getRecordType().equals(RecordType.PARSING)) {
			cursor.add(rec);
		}
		// cursor.getKey();
		// rec.setSqlid(cursor.getSqlId());
		return cursor;
	}

	public HashMap<Integer, Cursor> getByCursorNumber() {
		return byCursorNumber;
	}

	public ArrayList<Cursor> getCursors() {
		return cursors;
	}
}
