package se.de.hu_berlin.informatik.astlmbuilder.parsing.parser;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import se.de.hu_berlin.informatik.astlmbuilder.mapping.IModifierHandler;
import se.de.hu_berlin.informatik.astlmbuilder.mapping.IOperatorHandler;
import se.de.hu_berlin.informatik.astlmbuilder.mapping.ITypeHandler;
import se.de.hu_berlin.informatik.astlmbuilder.mapping.keywords.IBasicKeyWords;
import se.de.hu_berlin.informatik.astlmbuilder.mapping.keywords.IKeyWordProvider;
import se.de.hu_berlin.informatik.astlmbuilder.mapping.keywords.IKeyWordProvider.KeyWords;
import se.de.hu_berlin.informatik.astlmbuilder.parsing.InformationWrapper;
import se.de.hu_berlin.informatik.astlmbuilder.parsing.creator.INodeCreator;
import se.de.hu_berlin.informatik.astlmbuilder.parsing.dispatcher.IKeyWordDispatcher;
import se.de.hu_berlin.informatik.astlmbuilder.parsing.guesser.INodeGuesser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Interface that provides functionality to create AST nodes from String
 * representations.
 * <p>
 * General format for elements with <br>
 * maximum abstraction: {@code node_id}, and <br>
 * other abstraction level: {@code node_id[member_1][member_2]...[member_n]},
 * <br>
 * where each {@code member_k} is again an element itself.
 */
public interface ITokenParserBasics extends IModifierHandler, IOperatorHandler, ITypeHandler {

    final public static List<String> KEYWORD_DUMMY = Collections.emptyList();

    public IKeyWordDispatcher getDispatcher();

    public INodeGuesser getGuesser();

    public INodeCreator getCreator();

    public IKeyWordProvider<String> getKeyWordProvider();

    /**
     * Parses the given token and extracts the member data.
     * <p>
     * Expected token format: {@code [member_1]...[member_n]}.
     *
     * @param token the token to parse
     * @return the list of members (may be empty, but will not be null)
     * @throws IllegalArgumentException if the given token is of the wrong format
     */
    public static List<String> getMembers(String token) throws IllegalArgumentException {
        if (token.charAt(0) != IBasicKeyWords.GROUP_START
                || token.charAt(token.length() - 1) != IBasicKeyWords.GROUP_END) {
            throw new IllegalArgumentException("Illegal start or end: '" + token + "'.");
        }
        List<String> allMembers = new ArrayList<>();
        int depth = 0;
        int startIdx = 0;

        // this will fail if any actual values inside of the tokens contain '['
        // or ']'
        for (int idx = 0; idx < token.length(); ++idx) {
            switch (token.charAt(idx)) {
                case IBasicKeyWords.GROUP_START:
                    if (++depth == 1) { // mark this only as the start of a group if
                        // at depth 1
                        startIdx = idx + 1;
                    }
                    break;
                case IBasicKeyWords.GROUP_END:
                    if (--depth == 0) { // we are again at the top level
                        if (startIdx != idx) { // we should not add empty strings
                            // here
                            allMembers.add(token.substring(startIdx, idx));
                        }
                    }
                    if (depth < 0) {
                        throw new IllegalArgumentException("Illegal format: '" + token + "'.");
                    }
                    break;
                default:
                    break;
            }
        }
        if (depth != 0) { // should always have an equal number of opening and
            // closing brackets
            throw new IllegalArgumentException("Illegal format: '" + token + "'.");
        }
        return allMembers;
    }

    /**
     * Checks if the expected keyword matches the token and returns existing
     * member data tokens, if any. Returns an empty list, if only the keyword
     * exists, and returns null if the parsed keyword is (only) the null
     * keyword.
     * <p>
     * Expected token format: {@code id}, or {@code id[member_1]...[member_n]},
     * or {@code ~} for null.
     *
     * @param expectedKeyWord the expected keyword
     * @param token           the token to parse
     * @return the list of member data tokens or null
     * @throws IllegalArgumentException if the given token is of the wrong format
     */
    default public List<String> parseExpectedNodeMembersFromToken(KeyWords expectedKeyWord, String token)
            throws IllegalArgumentException {
        // token = removeOuterBrackets(token);
        char start = token.charAt(0);
        if (start == IBasicKeyWords.KEYWORD_NULL) { // return null if this is
            // the only char in the
            // given String
            if (token.length() == 1) {
                return null;
            } else {
                throw new IllegalArgumentException("Illegal null token: '" + token + "'.");
            }
        } else if (start != IBasicKeyWords.KEYWORD_LIST) { // create node from
            // entire String
            int firstSplitIndex = token.indexOf(IBasicKeyWords.GROUP_START);
            if (firstSplitIndex > 0) { // found split char
                // format: id,[member_1],...,[member_n]
                if (token.substring(0, firstSplitIndex).equals(getKeyWordProvider().getKeyWord(expectedKeyWord))) {
                    return getMembers(token.substring(firstSplitIndex));
                } else {
                    throw new IllegalArgumentException("Unexpected keyword: '" + token.substring(0, firstSplitIndex)
                            + "', expected: '" + expectedKeyWord + "'.");
                }
            } else { // found no split char
                // one is an enum and one is a string; still fails with short mapper
                if (token.equals(expectedKeyWord.toString())) { // format: id
                    return KEYWORD_DUMMY;
                } else {
                    // in case the short mapper is used the index has to be checked
                    // this could be put together with the check above or we change the expected keyword to be an index in the first place
                    // anyway this is only a temporary solution
                    String shortKeyword = Integer.toHexString(expectedKeyWord.ordinal());
                    if (shortKeyword.equals(token)) {
                        return KEYWORD_DUMMY;
                    }

                    throw new IllegalArgumentException(
                            "Unexpected keyword: '" + token + "', expected: '" + expectedKeyWord + "'.");
                }
            }
        } else { // this should not happen ever and should throw an exception
            throw new IllegalArgumentException("Illegal token: '" + token + "'.");
        }
    }

    /**
     * Parses a list with nodes of unknown class (only the superclass may be
     * known) and returns a list with nodes with the type of the given
     * superclass. May "guess" the list members based on available information,
     * if only the list keyword exists or the list is not complete.
     * <p>
     * Expected token format: {@code #xyz}, or
     * {@code #xyz[member_1]...[member_n]}, or {@code ~} for null. {@code xyz}
     * is the number of elements in the original list.
     *
     * @param expectedSuperClazz the type of nodes in the list that should be returned
     * @param token              the token to parse
     * @param info               the currently available information
     * @param <T>                the type of nodes in the returned list
     * @return the parsed node list
     * @throws IllegalArgumentException if the given token is of the wrong format
     * @throws ClassCastException       if a node of the wrong type is returned for one of the list members
     * @throws NumberFormatException    if the number of list elements can not be parsed
     */
    default public <T extends Node> NodeList<T> parseListFromToken(Class<T> expectedSuperClazz, String token,
                                                                   InformationWrapper info) throws IllegalArgumentException, NumberFormatException, ClassCastException {
        // token = removeOuterBrackets(token);
        char start = token.charAt(0);
        if (start == IBasicKeyWords.KEYWORD_LIST) { // create list from entire
            // String
            int firstSplitIndex = token.indexOf(IBasicKeyWords.GROUP_START);
            if (firstSplitIndex > 0) { // found split char
                int originalListSize = Integer.valueOf(token.substring(1, firstSplitIndex));
                List<String> listMembers = getMembers(token.substring(firstSplitIndex));
                NodeList<T> result = new NodeList<>();
                for (String member : listMembers) {
                    // we only know the superclass of the list members here...
                    result.add(parseNodeFromToken(expectedSuperClazz, member, info));
                }
                // fill with guessed nodes if too short...
                // TODO: it may be ok to vary the size of the returned list
                for (int i = result.size(); i < originalListSize; ++i) {
                    result.add(getGuesser().guessNode(expectedSuperClazz, info));
                }
                return result;
            } else { // only the list keyword + size
                int originalListSize = Integer.valueOf(token.substring(1));
                return getGuesser().guessList(expectedSuperClazz, originalListSize, info);
            }
        } else if (start == IBasicKeyWords.KEYWORD_NULL) { // return null if
            // this is the only
            // char in the given
            // String
            if (token.length() == 1) {
                return null;
            } else {
                throw new IllegalArgumentException("Illegal null token: '" + token + "'.");
            }
        } else { // this should not happen ever and should throw an exception
            throw new IllegalArgumentException("Illegal token: '" + token + "'.");
        }
    }

    /**
     * Parses a list with BodyDeclaration nodes and returns a list with the
     * parsed nodes. May "guess" the list members based on available
     * information, if only the list keyword exists or the list is not complete.
     * Calls {@link #parseListFromToken(Class, String, InformationWrapper)}.
     * <p>
     * Expected token format: {@code #xyz}, or
     * {@code #xyz[member_1]...[member_n]}, or {@code ~} for null. {@code xyz}
     * is the number of elements in the original list.
     *
     * @param token the token to parse
     * @param info  the currently available information
     * @return the parsed node list
     * @throws IllegalArgumentException if the given token is of the wrong format
     * @throws ClassCastException       if a node of the wrong type is returned for one of the list members
     * @throws NumberFormatException    if the number of list elements can not be parsed
     */
    @SuppressWarnings("unchecked")
    default public NodeList<BodyDeclaration<?>> parseBodyDeclarationListFromToken(String token, InformationWrapper info)
            throws IllegalArgumentException, NumberFormatException, ClassCastException {
        // sadly, we can not cast directly, so we have to use a workaround
        // here...
        NodeList<? super BodyDeclaration<?>> temp = parseListFromToken(BodyDeclaration.class, token, info);
        return (NodeList<BodyDeclaration<?>>) temp;
    }

    /**
     * Parses a list with TypeDeclaration nodes and returns a list with the
     * parsed nodes. May "guess" the list members based on available
     * information, if only the list keyword exists or the list is not complete.
     * Calls {@link #parseListFromToken(Class, String, InformationWrapper)}.
     * <p>
     * Expected token format: {@code #xyz}, or
     * {@code (#xyz,[member_1],...,[member_n])}, or {@code ~} for null.
     * {@code xyz} is the number of elements in the original list.
     *
     * @param token the token to parse
     * @param info  the currently available information
     * @return the parsed node list
     * @throws IllegalArgumentException if the given token is of the wrong format
     * @throws ClassCastException       if a node of the wrong type is returned for one of the list members
     * @throws NumberFormatException    if the number of list elements can not be parsed
     */
    @SuppressWarnings("unchecked")
    default public NodeList<TypeDeclaration<?>> parseTypeDeclarationListFromToken(String token, InformationWrapper info)
            throws IllegalArgumentException, NumberFormatException, ClassCastException {
        // sadly, we can not cast directly, so we have to use a workaround
        // here...
        NodeList<? super TypeDeclaration<?>> temp = parseListFromToken(TypeDeclaration.class, token, info);
        return (NodeList<TypeDeclaration<?>>) temp;
    }

    /**
     * Parses a given token and returns a method identifier. May "guess" the
     * identifier based on available information if the identifier is abstract.
     * <p>
     * Expected token format: {@code identifier}, {@code _} for an abstract
     * identifier, or {@code ~} for null. {@code xyz} is the number of elements
     * in the original list.
     *
     * @param token the token to parse
     * @param info  the currently available information
     * @return the parsed method identifier
     * @throws IllegalArgumentException if the given token is of the wrong format
     */
    default public String parseMethodIdentifierFromToken(String token, InformationWrapper info)
            throws IllegalArgumentException {
        char start = token.charAt(0);
        if (start == IBasicKeyWords.KEYWORD_ABSTRACT) { // method identifier is
            // abstract
            if (token.length() == 1) {
                return getGuesser().guessMethodIdentifier(info);
            } else {
                throw new IllegalArgumentException("Illegal abstract identifier token: '" + token + "'.");
            }
        } else if (start == IBasicKeyWords.KEYWORD_NULL) { // return null if
            // this is the only
            // char in the given
            // String
            if (token.length() == 1) {
                return null;
            } else {
                throw new IllegalArgumentException("Illegal null token: '" + token + "'.");
            }
        } else if (start == IBasicKeyWords.KEYWORD_LIST) { // this should not be
            // the case
            throw new IllegalArgumentException("Illegal null token: '" + token + "'.");
        } else {
            return parseStringValueFromToken(token);
        }
    }

    /**
     * Parses a node of unknown class (only the superclass may be known) and
     * returns a node with the type of the given superclass.
     * <p>
     * Expected token format: {@code id} or {@code id[member_1]...[member_n]} or
     * {@code ~} for null.
     *
     * @param expectedSuperClazz the type of nodes that should be returned
     * @param token              the token to parse
     * @param info               the currently available information
     * @param <T>                the type of returned nodes
     * @return the parsed node
     * @throws IllegalArgumentException if the given token is of the wrong format
     * @throws ClassCastException       if a node of the wrong type is returned
     */
    default public <T extends Node> T parseNodeFromToken(Class<T> expectedSuperClazz, String token,
                                                         InformationWrapper info) throws IllegalArgumentException, ClassCastException {
        // token = removeOuterBrackets(token);
        char start = token.charAt(0);
        if (start == IBasicKeyWords.KEYWORD_NULL) { // return null if this is
            // the only char in the
            // given String
            if (token.length() == 1) {
                return null;
            } else {
                throw new IllegalArgumentException("Illegal null token: '" + token + "'.");
            }
        } else if (start != IBasicKeyWords.KEYWORD_LIST) { // create node from
            // entire String
            int firstSplitIndex = token.indexOf(IBasicKeyWords.GROUP_START);
            if (firstSplitIndex > 0) { // found split char
                return expectedSuperClazz.cast(
                        getDispatcher().dispatch(
                                getKeyWordProvider().StringToKeyWord(token.substring(0, firstSplitIndex)), token,
                                info.getCopy()));
            } else { // no split char, probably just the keyword
                return expectedSuperClazz.cast(
                        getDispatcher().dispatch(getKeyWordProvider().StringToKeyWord(token), token, info.getCopy()));
            }
        } else { // this should not happen ever and should throw an exception
            throw new IllegalArgumentException("Illegal token: '" + token + "'.");
        }
    }

    /**
     * Parses a node of unknown class and returns the result. Calls
     * {@link #parseNodeFromToken(Class, String, InformationWrapper)} with the
     * basic Node class.
     * <p>
     * Expected token format: {@code id} or
     * {@code (id,[member_1],...,[member_n])} or {@code ~} for null.
     *
     * @param token the token to parse
     * @param info  the currently available information
     * @return the parsed node
     * @throws IllegalArgumentException if the given token is of the wrong format
     * @throws ClassCastException       if a node of the wrong type is returned
     */
    default public Node parseNodeFromToken(String token, InformationWrapper info)
            throws IllegalArgumentException, ClassCastException {
        return parseNodeFromToken(Node.class, token, info);
    }

    /**
     * Splits the given token into its members, if any. May return null, if the
     * given token is the null keyword. Checks if the returned member data list
     * has the expected length or is empty.
     * <p>
     * Expected token format: {@code id} or
     * {@code (id,[member_1],...,[member_n])} or {@code ~} for null.
     *
     * @param token               the token to parse
     * @param expectedKeyWord     the expected keyword
     * @param expectedMemberCount the expected member data count
     * @return a list of member data tokens or null
     * @throws IllegalArgumentException if the given token is of the wrong format
     */
    public default List<String> parseAndCheckMembers(String token, KeyWords expectedKeyWord, int expectedMemberCount)
            throws IllegalArgumentException {
        List<String> memberData = parseExpectedNodeMembersFromToken(expectedKeyWord, token);
        if (memberData == null) { // token: ~
            return null;
        } else if (memberData == KEYWORD_DUMMY // token: id
                || memberData.size() == expectedMemberCount) { // (id,[member_1],...,[member_expectedMemberCount])
            return memberData;
        } else {
            throw new IllegalArgumentException("Member token count does not match node constructor arguments.");
        }
    }

    // /**
    // * Tries to "guess" a node of the expected type based on the given keyword
    // * and the available information.
    // * @param expectedSuperClazz
    // * the expected type of the node
    // * @param keyWord
    // * the keyword
    // * @param info
    // * the currently available information
    // * @return a node of the expected type
    // * @param <T>
    // * the type of node returned
    // * @throws ClassCastException
    // * if a node of the wrong type is returned
    // */
    // default public <T extends Node> T guessNodeFromKeyWord(Class<T>
    // expectedSuperClazz, String keyWord,
    // InformationWrapper info) throws ClassCastException {
    // return expectedSuperClazz
    // .cast(getDispatcher().dispatchAndGuess(getKeyWordProvider().StringToKeyWord(keyWord),
    // info));
    // }

}
