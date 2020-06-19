package org.javautil.dex.parser;

import java.io.IOException;
import java.util.ArrayList;

import org.javautil.dex.dexterous.Dexterous;

public class Inject extends AbstractCommandParser {
	/**
	 * use document '/scratch/filename.xml'
	 *
	 * inject as [ sibling | child ] of "state[@id='range']" with columns as
	 * [text | attribute]
	 *
	 * @throws ParseException
	 *
	 */
	/*
	 *
	 *
	 * @throws ParseException @throws IOException
	 */

	@Override
	public void parse() throws IOException, ParseException {
		final ArrayList<String> tokens = new ArrayList<String>();
		String xpath = null;
		NodeRelationship nodeRelationship = null;

		String token = null;
		validateState();
		final Lexer lexer = getLexer();
		boolean asElement = true;
		try {

			ensure(getToken(), "as");
			final String relationshipName = getToken();
			ensure(relationshipName, new String[] { "sibling", "child" });
			// if ("sibling".equalsIgnoreCase(relationshipName)) {
			// String preposition = lexer.next();
			// ensure(preposition,"sibling");
			// } else if ("child".equalsIgnoreCase(relationshipName)) {
			//
			// }
			// String preposition = lexer.next();
			ensure(getToken(), "of");

			nodeRelationship = NodeRelationship.valueOf(relationshipName
					.toUpperCase());
			// String preposition = lexer.next();
			xpath = getToken();
			ensure(token = getToken(), "with");
			ensure(token = getToken(), new String[] { "column", "columns" });
			ensure(token = getToken(), "as");

			final String type = getToken();
			ensure(type, new String[] { "text", "attribute" });

			if (type.equalsIgnoreCase("text")) {
				asElement = false;
			}
		} catch (final ParseException pe) {
			throw new ParseException(pe.getMessage() + " in  the clause "
					+ getTokensAsString());
		}
		final Dexterous dexterous = getDexterous();
		dexterous.setDocumentTemplateXpath(xpath, nodeRelationship, asElement);

	}

}
