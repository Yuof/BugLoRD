package se.de.hu_berlin.informatik.astlmbuilder.parser;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;

import se.de.hu_berlin.informatik.astlmbuilder.mapping.keywords.IKeyWordProvider;
import se.de.hu_berlin.informatik.astlmbuilder.mapping.keywords.KeyWordConstantsShort;
import se.de.hu_berlin.informatik.astlmbuilder.parser.dispatcher.IKeyWordDispatcher;
import se.de.hu_berlin.informatik.astlmbuilder.parser.dispatcher.KeyWordDispatcherShort;

/**
 * A simple implementation of the token parser using the short keywords
 */
public class TokenParserShort implements ITokenParser{

	IKeyWordProvider<String> kwp = new KeyWordConstantsShort();
	IKeyWordDispatcher kwd = new KeyWordDispatcherShort();
	
	@Override
	public IKeyWordProvider<String> getKeyWordProvider() {
		return kwp;
	}

	@Override
	public IKeyWordDispatcher getDispatcher() {
		return kwd;
	}

	@Override
	public <T extends Node> T guessNodeFromKeyWord(Class<T> expectedSuperClazz, String keyWord,
			InformationWrapper info) {
		// TODO implement
		return null;
	}

	@Override
	public <T extends Node> T guessNode(Class<T> expectedSuperClazz, InformationWrapper info) {
		// TODO implement
		return null;
	}

	@Override
	public <T extends Node> NodeList<T> guessList(Class<T> expectedSuperClazz, InformationWrapper info) {
		// TODO implement
		return null;
	}

}
