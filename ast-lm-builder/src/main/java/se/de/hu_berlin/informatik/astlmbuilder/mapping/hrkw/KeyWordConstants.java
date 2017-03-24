package se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.body.ModifierSet;

import se.de.hu_berlin.informatik.astlmbuilder.mapping.IBasicMapper;
import se.de.hu_berlin.informatik.astlmbuilder.mapping.IKeyWordProvider;

public class KeyWordConstants implements IBasicMapper, IKeyWordProvider {

	public final String TYPE_PARAMETERS_START = KEYWORD_MARKER + "TYPE_PARS";

	public final String COMPILATION_UNIT = KEYWORD_MARKER + "COMP_UNIT";
	public final String LINE_COMMENT = KEYWORD_MARKER + "LINE_COMMENT";
	public final String BLOCK_COMMENT = KEYWORD_MARKER + "BLOCK_COMMENT";
	public final String JAVADOC_COMMENT = KEYWORD_MARKER + "JAVADOC_COMMENT";

	public final String CONSTRUCTOR_DECLARATION = KEYWORD_MARKER + "CNSTR_DEC";
	public final String INITIALIZER_DECLARATION = KEYWORD_MARKER + "INIT_DEC";
	public final String ENUM_CONSTANT_DECLARATION = KEYWORD_MARKER + "ENUM_CONST_DEC";
	public final String VARIABLE_DECLARATION = KEYWORD_MARKER + "VAR_DEC";
	public final String ENUM_DECLARATION = KEYWORD_MARKER + "ENUM_DEC";
	public final String ANNOTATION_DECLARATION = KEYWORD_MARKER + "ANN_DEC";
	public final String ANNOTATION_MEMBER_DECLARATION = KEYWORD_MARKER + "ANN_MEMBER_DEC";
	public final String EMPTY_MEMBER_DECLARATION = KEYWORD_MARKER + "EMPTY_MEMBER_DEC";
	public final String EMPTY_TYPE_DECLARATION = KEYWORD_MARKER + "EMPTY_TYPE_DEC";
	public final String WHILE_STATEMENT = KEYWORD_MARKER + "WHILE";
	public final String TRY_STATEMENT = KEYWORD_MARKER + "TRY";
	public final String THROW_STATEMENT = KEYWORD_MARKER + "THROW";
	public final String THROWS_STATEMENT = KEYWORD_MARKER + "THROWS";
	public final String SYNCHRONIZED_STATEMENT = KEYWORD_MARKER + "SYNC";
	public final String SWITCH_STATEMENT = KEYWORD_MARKER + "SWITCH";
	public final String SWITCH_ENTRY_STATEMENT = KEYWORD_MARKER + "SWITCH_ENTRY";
	public final String RETURN_STATEMENT = KEYWORD_MARKER + "RETURN";
	public final String LABELED_STATEMENT = KEYWORD_MARKER + "LABELED";
	public final String IF_STATEMENT = KEYWORD_MARKER + "IF";
	public final String ELSE_STATEMENT = KEYWORD_MARKER + "ELSE";
	public final String FOR_STATEMENT = KEYWORD_MARKER + "FOR";
	public final String FOR_EACH_STATEMENT = KEYWORD_MARKER + "FOR_EACH";
	public final String EXPRESSION_STATEMENT = KEYWORD_MARKER + "EXPR_STMT";
	public final String EXPLICIT_CONSTRUCTOR_STATEMENT = KEYWORD_MARKER + "EXPL_CONSTR";
	public final String EMPTY_STATEMENT = KEYWORD_MARKER + "EMPTY";
	public final String DO_STATEMENT = KEYWORD_MARKER + "DO";
	public final String CONTINUE_STATEMENT = KEYWORD_MARKER + "CONTINUE";
	public final String CATCH_CLAUSE_STATEMENT = KEYWORD_MARKER + "CATCH";
	public final String BLOCK_STATEMENT = KEYWORD_MARKER + "BLOCK";
	public final String VARIABLE_DECLARATION_ID = KEYWORD_MARKER + "VAR_DEC_ID";
	public final String VARIABLE_DECLARATION_EXPRESSION = KEYWORD_MARKER + "VAR_DEC_EXPR";
	public final String TYPE_EXPRESSION = KEYWORD_MARKER + "TYPE_EXPR";
	public final String SUPER_EXPRESSION = KEYWORD_MARKER + "SUPER";
	public final String QUALIFIED_NAME_EXPRESSION = KEYWORD_MARKER + "QUALIFIED_NAME";
	public final String NULL_LITERAL_EXPRESSION = KEYWORD_MARKER + "NULL_LIT";
	public final String METHOD_REFERENCE_EXPRESSION = KEYWORD_MARKER + "MT_REF";
	public final String BODY_STMT = KEYWORD_MARKER + "BODY";
	public final String LONG_LITERAL_MIN_VALUE_EXPRESSION = KEYWORD_MARKER + "LONG_LIT_MIN";
	public final String LAMBDA_EXPRESSION = KEYWORD_MARKER + "LAMBDA";
	public final String INTEGER_LITERAL_MIN_VALUE_EXPRESSION = KEYWORD_MARKER + "INT_LIT_MIN";
	public final String INSTANCEOF_EXPRESSION = KEYWORD_MARKER + "INSTANCEOF";
	public final String FIELD_ACCESS_EXPRESSION = KEYWORD_MARKER + "FIELD_ACC";
	public final String CONDITIONAL_EXPRESSION = KEYWORD_MARKER + "CONDITION";
	public final String CLASS_EXPRESSION = KEYWORD_MARKER + "CLASS";
	public final String CAST_EXPRESSION = KEYWORD_MARKER + "CAST";
	public final String ASSIGN_EXPRESSION = KEYWORD_MARKER + "ASSIGN";
	public final String ARRAY_INIT_EXPRESSION = KEYWORD_MARKER + "INIT_ARR";
	public final String ARRAY_CREATE_EXPRESSION = KEYWORD_MARKER + "CREATE_ARR";
	public final String ARRAY_ACCESS_EXPRESSION = KEYWORD_MARKER + "ARR_ACC";
	public final String PACKAGE_DECLARATION = KEYWORD_MARKER + "P_DEC";
	public final String IMPORT_DECLARATION = KEYWORD_MARKER + "IMP_DEC";
	public final String FIELD_DECLARATION = KEYWORD_MARKER + "FIELD_DEC";
	public final String CLASS_OR_INTERFACE_TYPE = KEYWORD_MARKER + "CI_TYPE";
	public final String CLASS_OR_INTERFACE_DECLARATION = KEYWORD_MARKER + "CI_DEC";
	public final String CLASS_DECLARATION = KEYWORD_MARKER + "CLS_DEC";
	public final String INTERFACE_DECLARATION = KEYWORD_MARKER + "INTF_DEC";
	public final String EXTENDS_STATEMENT = KEYWORD_MARKER + "EXTENDS";
	public final String IMPLEMENTS_STATEMENT = KEYWORD_MARKER + "IMPLEMENTS";
	public final String METHOD_DECLARATION = KEYWORD_MARKER + "MT_DEC";
	public final String BINARY_EXPRESSION = KEYWORD_MARKER + "BIN_EXPR";
	public final String UNARY_EXPRESSION = KEYWORD_MARKER + "UNARY_EXPR";
	public final String METHOD_CALL_EXPRESSION = KEYWORD_MARKER + "MT_CALL";
	// if a private method is called we handle it differently
	public final String PRIVATE_METHOD_CALL_EXPRESSION = KEYWORD_MARKER + "MT_CALL_PRIV";
	public final String NAME_EXPRESSION = KEYWORD_MARKER + "NAME_EXPR";
	public final String INTEGER_LITERAL_EXPRESSION = KEYWORD_MARKER + "INT_LIT";
	public final String DOUBLE_LITERAL_EXPRESSION = KEYWORD_MARKER + "DOUBLE_LIT";
	public final String STRING_LITERAL_EXPRESSION = KEYWORD_MARKER + "STR_LIT";
	public final String BOOLEAN_LITERAL_EXPRESSION = KEYWORD_MARKER + "BOOL_LIT";
	public final String CHAR_LITERAL_EXPRESSION = KEYWORD_MARKER + "CHAR_LIT";
	public final String LONG_LITERAL_EXPRESSION = KEYWORD_MARKER + "LONG_LIT";
	public final String THIS_EXPRESSION = KEYWORD_MARKER + "THIS";
	public final String BREAK = KEYWORD_MARKER + "BREAK";
	public final String OBJ_CREATE_EXPRESSION = KEYWORD_MARKER + "NEW_OBJ";
	public final String MARKER_ANNOTATION_EXPRESSION = KEYWORD_MARKER + "MARKER_ANN_EXPR";
	public final String NORMAL_ANNOTATION_EXPRESSION = KEYWORD_MARKER + "NORMAL_ANN_EXPR";
	public final String SINGLE_MEMBER_ANNOTATION_EXPRESSION = KEYWORD_MARKER + "SM_ANN_EXPR";

	public final String PARAMETER = KEYWORD_MARKER + "PAR";
	public final String MULTI_TYPE_PARAMETER = KEYWORD_MARKER + "MTYPE_PAR";
	public final String ENCLOSED_EXPRESSION = KEYWORD_MARKER + "ENCLOSED";
	public final String ASSERT_STMT = KEYWORD_MARKER + "ASSERT";
	public final String MEMBER_VALUE_PAIR = KEYWORD_MARKER + "MV_PAIR";

	public final String TYPE_DECLARATION_STATEMENT = KEYWORD_MARKER + "TYPE_DEC";
	public final String TYPE_REFERENCE = KEYWORD_MARKER + "REF_TYPE";
	public final String TYPE_PRIMITIVE = KEYWORD_MARKER + "PRIM_TYPE";
	public final String TYPE_UNION = KEYWORD_MARKER + "UNION_TYPE";
	public final String TYPE_INTERSECTION = KEYWORD_MARKER + "INTERSECT_TYPE";
	public final String TYPE_PAR = KEYWORD_MARKER + "TYPE_PAR";
	public final String TYPE_WILDCARD = KEYWORD_MARKER + "WILDCARD_TYPE";
	public final String TYPE_VOID = KEYWORD_MARKER + "VOID_TYPE";
	public final String TYPE_UNKNOWN = KEYWORD_MARKER + "UNKNOWN_TYPE";

	public final String UNKNOWN = KEYWORD_MARKER + "T_UNKNOWN";

	// closing tags for some special nodes
	public final String END_SUFFIX = "_END";
	public final String CLOSING_MDEC = METHOD_DECLARATION + END_SUFFIX;
	public final String CLOSING_CNSTR = CONSTRUCTOR_DECLARATION + END_SUFFIX;
	public final String CLOSING_IF = IF_STATEMENT + END_SUFFIX;
	public final String CLOSING_WHILE = WHILE_STATEMENT + END_SUFFIX;
	public final String CLOSING_FOR = FOR_STATEMENT + END_SUFFIX;
	public final String CLOSING_TRY = TRY_STATEMENT + END_SUFFIX;
	public final String CLOSING_CATCH = CATCH_CLAUSE_STATEMENT + END_SUFFIX;
	public final String CLOSING_FOR_EACH = FOR_EACH_STATEMENT + END_SUFFIX;
	public final String CLOSING_DO = DO_STATEMENT + END_SUFFIX;
	public final String CLOSING_SWITCH = SWITCH_STATEMENT + END_SUFFIX;
	public final String CLOSING_ENCLOSED = ENCLOSED_EXPRESSION + END_SUFFIX;
	public final String CLOSING_BLOCK_STMT = BLOCK_STATEMENT + END_SUFFIX;
	public final String CLOSING_EXPRESSION_STMT = EXPRESSION_STATEMENT + END_SUFFIX;
	public final String CLOSING_COMPILATION_UNIT = COMPILATION_UNIT + END_SUFFIX;

	// this was previously in the modifier mapper but there is no reason to keep
	// it external
	// because we will never mix the short keywords with long modifier
	// identifications
	private final String PRIV = "PRIV";
	private final String PUB = "PUB";
	private final String PROT = "PROT";
	private final String ABS = "ABS";
	private final String STATIC = "STATIC";
	private final String FINAL = "FINAL";
	private final String NATIVE = "NATIVE";
	private final String STRICTFP = "STRICTFP";
	private final String SYNC = "SYNC";
	private final String TRANS = "TRANS";
	private final String VOLATILE = "VOLATILE";

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getModifierEnclosed(int)
	 */
	@Override
	public String getModifierEnclosed(final int modifiers) {
		return KeyWordConstants.GROUP_START + getModifier(modifiers) + KeyWordConstants.GROUP_END;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getModifier(int)
	 */
	@Override
	public String getModifier(final int modifiers) {
		List<String> result = new ArrayList<>();

		if (ModifierSet.isPrivate(modifiers)) {
			result.add(PRIV);
		}
		if (ModifierSet.isPublic(modifiers)) {
			result.add(PUB);
		}
		if (ModifierSet.isProtected(modifiers)) {
			result.add(PROT);
		}
		if (ModifierSet.isAbstract(modifiers)) {
			result.add(ABS);
		}
		if (ModifierSet.isStatic(modifiers)) {
			result.add(STATIC);
		}
		if (ModifierSet.isFinal(modifiers)) {
			result.add(FINAL);
		}
		if (ModifierSet.isNative(modifiers)) {
			result.add(NATIVE);
		}
		if (ModifierSet.isStrictfp(modifiers)) {
			result.add(STRICTFP);
		}
		if (ModifierSet.isSynchronized(modifiers)) {
			result.add(SYNC);
		}
		if (ModifierSet.isTransient(modifiers)) {
			result.add(TRANS);
		}
		if (ModifierSet.isVolatile(modifiers)) {
			result.add(VOLATILE);
		}

		return String.join(",", result);
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getAllModsAsInt(java.lang.String)
	 */
	@Override
	public int getAllModsAsInt(String aAllMods) {
		int result = 0; // 0 means no mods att all

		for (String s : aAllMods.split(",")) {
			result = getOrAddModifier(s, result);
		}

		return result;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getOrAddModifier(java.lang.String, int)
	 */
	@Override
	public int getOrAddModifier(String aMod, int aBase) {
		int result = aBase; // call by value, I know but i prefer to name it
							// result

		int mappedMod = 0;

		switch (aMod) {
		case PRIV:
			mappedMod = ModifierSet.PRIVATE;
			break;
		case PUB:
			mappedMod = ModifierSet.PUBLIC;
			break;
		case PROT:
			mappedMod = ModifierSet.PROTECTED;
			break;
		case ABS:
			mappedMod = ModifierSet.ABSTRACT;
			break;
		case STATIC:
			mappedMod = ModifierSet.STATIC;
			break;
		case FINAL:
			mappedMod = ModifierSet.FINAL;
			break;
		case NATIVE:
			mappedMod = ModifierSet.NATIVE;
			break;
		case STRICTFP:
			mappedMod = ModifierSet.STRICTFP;
			break;
		case TRANS:
			mappedMod = ModifierSet.TRANSIENT;
			break;
		case VOLATILE:
			mappedMod = ModifierSet.VOLATILE;
			break;
		default:
			mappedMod = 0;
			break; // added no modifier because the given String was unknown
		}

		return ModifierSet.addModifier(result, mappedMod);
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getTypeParametersStart()
	 */
	@Override
	public String getTypeParametersStart() {
		return TYPE_PARAMETERS_START;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getCompilationUnit()
	 */
	@Override
	public String getCompilationUnit() {
		return COMPILATION_UNIT;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getLineComment()
	 */
	@Override
	public String getLineComment() {
		return LINE_COMMENT;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getBlockComment()
	 */
	@Override
	public String getBlockComment() {
		return BLOCK_COMMENT;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getJavadocComment()
	 */
	@Override
	public String getJavadocComment() {
		return JAVADOC_COMMENT;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getConstructorDeclaration()
	 */
	@Override
	public String getConstructorDeclaration() {
		return CONSTRUCTOR_DECLARATION;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getInitializerDeclaration()
	 */
	@Override
	public String getInitializerDeclaration() {
		return INITIALIZER_DECLARATION;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getEnumConstantDeclaration()
	 */
	@Override
	public String getEnumConstantDeclaration() {
		return ENUM_CONSTANT_DECLARATION;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getVariableDeclaration()
	 */
	@Override
	public String getVariableDeclaration() {
		return VARIABLE_DECLARATION;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getEnumDeclaration()
	 */
	@Override
	public String getEnumDeclaration() {
		return ENUM_DECLARATION;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getAnnotationDeclaration()
	 */
	@Override
	public String getAnnotationDeclaration() {
		return ANNOTATION_DECLARATION;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getAnnotationMemberDeclaration()
	 */
	@Override
	public String getAnnotationMemberDeclaration() {
		return ANNOTATION_MEMBER_DECLARATION;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getEmptyMemberDeclaration()
	 */
	@Override
	public String getEmptyMemberDeclaration() {
		return EMPTY_MEMBER_DECLARATION;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getEmptyTypeDeclaration()
	 */
	@Override
	public String getEmptyTypeDeclaration() {
		return EMPTY_TYPE_DECLARATION;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getWhileStatement()
	 */
	@Override
	public String getWhileStatement() {
		return WHILE_STATEMENT;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getTryStatement()
	 */
	@Override
	public String getTryStatement() {
		return TRY_STATEMENT;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getThrowStatement()
	 */
	@Override
	public String getThrowStatement() {
		return THROW_STATEMENT;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getThrowsStatement()
	 */
	@Override
	public String getThrowsStatement() {
		return THROWS_STATEMENT;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getSynchronizedStatement()
	 */
	@Override
	public String getSynchronizedStatement() {
		return SYNCHRONIZED_STATEMENT;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getSwitchStatement()
	 */
	@Override
	public String getSwitchStatement() {
		return SWITCH_STATEMENT;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getSwitchEntryStatement()
	 */
	@Override
	public String getSwitchEntryStatement() {
		return SWITCH_ENTRY_STATEMENT;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getReturnStatement()
	 */
	@Override
	public String getReturnStatement() {
		return RETURN_STATEMENT;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getLabeledStatement()
	 */
	@Override
	public String getLabeledStatement() {
		return LABELED_STATEMENT;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getIfStatement()
	 */
	@Override
	public String getIfStatement() {
		return IF_STATEMENT;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getElseStatement()
	 */
	@Override
	public String getElseStatement() {
		return ELSE_STATEMENT;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getForStatement()
	 */
	@Override
	public String getForStatement() {
		return FOR_STATEMENT;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getForEachStatement()
	 */
	@Override
	public String getForEachStatement() {
		return FOR_EACH_STATEMENT;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getExpressionStatement()
	 */
	@Override
	public String getExpressionStatement() {
		return EXPRESSION_STATEMENT;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getExplicitConstructorStatement()
	 */
	@Override
	public String getExplicitConstructorStatement() {
		return EXPLICIT_CONSTRUCTOR_STATEMENT;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getEmptyStatement()
	 */
	@Override
	public String getEmptyStatement() {
		return EMPTY_STATEMENT;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getDoStatement()
	 */
	@Override
	public String getDoStatement() {
		return DO_STATEMENT;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getContinueStatement()
	 */
	@Override
	public String getContinueStatement() {
		return CONTINUE_STATEMENT;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getCatchClauseStatement()
	 */
	@Override
	public String getCatchClauseStatement() {
		return CATCH_CLAUSE_STATEMENT;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getBlockStatement()
	 */
	@Override
	public String getBlockStatement() {
		return BLOCK_STATEMENT;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getVariableDeclarationId()
	 */
	@Override
	public String getVariableDeclarationId() {
		return VARIABLE_DECLARATION_ID;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getVariableDeclarationExpression()
	 */
	@Override
	public String getVariableDeclarationExpression() {
		return VARIABLE_DECLARATION_EXPRESSION;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getTypeExpression()
	 */
	@Override
	public String getTypeExpression() {
		return TYPE_EXPRESSION;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getSuperExpression()
	 */
	@Override
	public String getSuperExpression() {
		return SUPER_EXPRESSION;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getQualifiedNameExpression()
	 */
	@Override
	public String getQualifiedNameExpression() {
		return QUALIFIED_NAME_EXPRESSION;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getNullLiteralExpression()
	 */
	@Override
	public String getNullLiteralExpression() {
		return NULL_LITERAL_EXPRESSION;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getMethodReferenceExpression()
	 */
	@Override
	public String getMethodReferenceExpression() {
		return METHOD_REFERENCE_EXPRESSION;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getBodyStmt()
	 */
	@Override
	public String getBodyStmt() {
		return BODY_STMT;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getLongLiteralMinValueExpression()
	 */
	@Override
	public String getLongLiteralMinValueExpression() {
		return LONG_LITERAL_MIN_VALUE_EXPRESSION;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getLambdaExpression()
	 */
	@Override
	public String getLambdaExpression() {
		return LAMBDA_EXPRESSION;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getIntegerLiteralMinValueExpression()
	 */
	@Override
	public String getIntegerLiteralMinValueExpression() {
		return INTEGER_LITERAL_MIN_VALUE_EXPRESSION;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getInstanceofExpression()
	 */
	@Override
	public String getInstanceofExpression() {
		return INSTANCEOF_EXPRESSION;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getFieldAccessExpression()
	 */
	@Override
	public String getFieldAccessExpression() {
		return FIELD_ACCESS_EXPRESSION;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getConditionalExpression()
	 */
	@Override
	public String getConditionalExpression() {
		return CONDITIONAL_EXPRESSION;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getClassExpression()
	 */
	@Override
	public String getClassExpression() {
		return CLASS_EXPRESSION;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getCastExpression()
	 */
	@Override
	public String getCastExpression() {
		return CAST_EXPRESSION;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getAssignExpression()
	 */
	@Override
	public String getAssignExpression() {
		return ASSIGN_EXPRESSION;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getArrayInitExpression()
	 */
	@Override
	public String getArrayInitExpression() {
		return ARRAY_INIT_EXPRESSION;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getArrayCreateExpression()
	 */
	@Override
	public String getArrayCreateExpression() {
		return ARRAY_CREATE_EXPRESSION;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getArrayAccessExpression()
	 */
	@Override
	public String getArrayAccessExpression() {
		return ARRAY_ACCESS_EXPRESSION;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getPackageDeclaration()
	 */
	@Override
	public String getPackageDeclaration() {
		return PACKAGE_DECLARATION;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getImportDeclaration()
	 */
	@Override
	public String getImportDeclaration() {
		return IMPORT_DECLARATION;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getFieldDeclaration()
	 */
	@Override
	public String getFieldDeclaration() {
		return FIELD_DECLARATION;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getClassOrInterfaceType()
	 */
	@Override
	public String getClassOrInterfaceType() {
		return CLASS_OR_INTERFACE_TYPE;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getClassOrInterfaceDeclaration()
	 */
	@Override
	public String getClassOrInterfaceDeclaration() {
		return CLASS_OR_INTERFACE_DECLARATION;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getClassDeclaration()
	 */
	@Override
	public String getClassDeclaration() {
		return CLASS_DECLARATION;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getInterfaceDeclaration()
	 */
	@Override
	public String getInterfaceDeclaration() {
		return INTERFACE_DECLARATION;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getExtendsStatement()
	 */
	@Override
	public String getExtendsStatement() {
		return EXTENDS_STATEMENT;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getImplementsStatement()
	 */
	@Override
	public String getImplementsStatement() {
		return IMPLEMENTS_STATEMENT;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getMethodDeclaration()
	 */
	@Override
	public String getMethodDeclaration() {
		return METHOD_DECLARATION;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getBinaryExpression()
	 */
	@Override
	public String getBinaryExpression() {
		return BINARY_EXPRESSION;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getUnaryExpression()
	 */
	@Override
	public String getUnaryExpression() {
		return UNARY_EXPRESSION;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getMethodCallExpression()
	 */
	@Override
	public String getMethodCallExpression() {
		return METHOD_CALL_EXPRESSION;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getPrivateMethodCallExpression()
	 */
	@Override
	public String getPrivateMethodCallExpression() {
		return PRIVATE_METHOD_CALL_EXPRESSION;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getNameExpression()
	 */
	@Override
	public String getNameExpression() {
		return NAME_EXPRESSION;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getIntegerLiteralExpression()
	 */
	@Override
	public String getIntegerLiteralExpression() {
		return INTEGER_LITERAL_EXPRESSION;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getDoubleLiteralExpression()
	 */
	@Override
	public String getDoubleLiteralExpression() {
		return DOUBLE_LITERAL_EXPRESSION;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getStringLiteralExpression()
	 */
	@Override
	public String getStringLiteralExpression() {
		return STRING_LITERAL_EXPRESSION;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getBooleanLiteralExpression()
	 */
	@Override
	public String getBooleanLiteralExpression() {
		return BOOLEAN_LITERAL_EXPRESSION;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getCharLiteralExpression()
	 */
	@Override
	public String getCharLiteralExpression() {
		return CHAR_LITERAL_EXPRESSION;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getLongLiteralExpression()
	 */
	@Override
	public String getLongLiteralExpression() {
		return LONG_LITERAL_EXPRESSION;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getThisExpression()
	 */
	@Override
	public String getThisExpression() {
		return THIS_EXPRESSION;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getBreak()
	 */
	@Override
	public String getBreak() {
		return BREAK;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getObjCreateExpression()
	 */
	@Override
	public String getObjCreateExpression() {
		return OBJ_CREATE_EXPRESSION;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getMarkerAnnotationExpression()
	 */
	@Override
	public String getMarkerAnnotationExpression() {
		return MARKER_ANNOTATION_EXPRESSION;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getNormalAnnotationExpression()
	 */
	@Override
	public String getNormalAnnotationExpression() {
		return NORMAL_ANNOTATION_EXPRESSION;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getSingleMemberAnnotationExpression()
	 */
	@Override
	public String getSingleMemberAnnotationExpression() {
		return SINGLE_MEMBER_ANNOTATION_EXPRESSION;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getParameter()
	 */
	@Override
	public String getParameter() {
		return PARAMETER;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getMultiTypeParameter()
	 */
	@Override
	public String getMultiTypeParameter() {
		return MULTI_TYPE_PARAMETER;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getEnclosedExpression()
	 */
	@Override
	public String getEnclosedExpression() {
		return ENCLOSED_EXPRESSION;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getAssertStmt()
	 */
	@Override
	public String getAssertStmt() {
		return ASSERT_STMT;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getMemberValuePair()
	 */
	@Override
	public String getMemberValuePair() {
		return MEMBER_VALUE_PAIR;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getTypeDeclarationStatement()
	 */
	@Override
	public String getTypeDeclarationStatement() {
		return TYPE_DECLARATION_STATEMENT;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getTypeReference()
	 */
	@Override
	public String getTypeReference() {
		return TYPE_REFERENCE;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getTypePrimitive()
	 */
	@Override
	public String getTypePrimitive() {
		return TYPE_PRIMITIVE;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getTypeUnion()
	 */
	@Override
	public String getTypeUnion() {
		return TYPE_UNION;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getTypeIntersection()
	 */
	@Override
	public String getTypeIntersection() {
		return TYPE_INTERSECTION;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getTypePar()
	 */
	@Override
	public String getTypePar() {
		return TYPE_PAR;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getTypeWildcard()
	 */
	@Override
	public String getTypeWildcard() {
		return TYPE_WILDCARD;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getTypeVoid()
	 */
	@Override
	public String getTypeVoid() {
		return TYPE_VOID;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getTypeUnknown()
	 */
	@Override
	public String getTypeUnknown() {
		return TYPE_UNKNOWN;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getUnknown()
	 */
	@Override
	public String getUnknown() {
		return UNKNOWN;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getEndSuffix()
	 */
	@Override
	public String getEndSuffix() {
		return END_SUFFIX;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getClosingMdec()
	 */
	@Override
	public String getClosingMdec() {
		return CLOSING_MDEC;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getClosingCnstr()
	 */
	@Override
	public String getClosingCnstr() {
		return CLOSING_CNSTR;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getClosingIf()
	 */
	@Override
	public String getClosingIf() {
		return CLOSING_IF;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getClosingWhile()
	 */
	@Override
	public String getClosingWhile() {
		return CLOSING_WHILE;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getClosingFor()
	 */
	@Override
	public String getClosingFor() {
		return CLOSING_FOR;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getClosingTry()
	 */
	@Override
	public String getClosingTry() {
		return CLOSING_TRY;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getClosingCatch()
	 */
	@Override
	public String getClosingCatch() {
		return CLOSING_CATCH;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getClosingForEach()
	 */
	@Override
	public String getClosingForEach() {
		return CLOSING_FOR_EACH;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getClosingDo()
	 */
	@Override
	public String getClosingDo() {
		return CLOSING_DO;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getClosingSwitch()
	 */
	@Override
	public String getClosingSwitch() {
		return CLOSING_SWITCH;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getClosingEnclosed()
	 */
	@Override
	public String getClosingEnclosed() {
		return CLOSING_ENCLOSED;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getClosingBlockStmt()
	 */
	@Override
	public String getClosingBlockStmt() {
		return CLOSING_BLOCK_STMT;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getClosingExpressionStmt()
	 */
	@Override
	public String getClosingExpressionStmt() {
		return CLOSING_EXPRESSION_STMT;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getClosingCompilationUnit()
	 */
	@Override
	public String getClosingCompilationUnit() {
		return CLOSING_COMPILATION_UNIT;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getPriv()
	 */
	@Override
	public String getPriv() {
		return PRIV;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getPub()
	 */
	@Override
	public String getPub() {
		return PUB;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getProt()
	 */
	@Override
	public String getProt() {
		return PROT;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getAbs()
	 */
	@Override
	public String getAbs() {
		return ABS;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getStatic()
	 */
	@Override
	public String getStatic() {
		return STATIC;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getFinal()
	 */
	@Override
	public String getFinal() {
		return FINAL;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getNative()
	 */
	@Override
	public String getNative() {
		return NATIVE;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getStrictfp()
	 */
	@Override
	public String getStrictfp() {
		return STRICTFP;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getSync()
	 */
	@Override
	public String getSync() {
		return SYNC;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getTrans()
	 */
	@Override
	public String getTrans() {
		return TRANS;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.astlmbuilder.mapping.hrkw.IKeyWordProvider#getVolatile()
	 */
	@Override
	public String getVolatile() {
		return VOLATILE;
	}

}