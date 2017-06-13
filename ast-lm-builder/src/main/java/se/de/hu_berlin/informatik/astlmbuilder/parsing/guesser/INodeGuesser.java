package se.de.hu_berlin.informatik.astlmbuilder.parsing.guesser;

import com.github.javaparser.ast.ArrayCreationLevel;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.AnnotationMemberDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.InitializerDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.ArrayAccessExpr;
import com.github.javaparser.ast.expr.ArrayCreationExpr;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.CharLiteralExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.InstanceOfExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.SuperExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.TypeExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.modules.ModuleDeclaration;
import com.github.javaparser.ast.modules.ModuleExportsStmt;
import com.github.javaparser.ast.modules.ModuleOpensStmt;
import com.github.javaparser.ast.modules.ModuleProvidesStmt;
import com.github.javaparser.ast.modules.ModuleRequiresStmt;
import com.github.javaparser.ast.modules.ModuleStmt;
import com.github.javaparser.ast.modules.ModuleUsesStmt;
import com.github.javaparser.ast.stmt.AssertStmt;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.BreakStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ContinueStmt;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.ForeachStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.LabeledStmt;
import com.github.javaparser.ast.stmt.LocalClassDeclarationStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.SwitchEntryStmt;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.stmt.SynchronizedStmt;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.type.ArrayType;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.IntersectionType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.TypeParameter;
import com.github.javaparser.ast.type.UnionType;
import com.github.javaparser.ast.type.UnknownType;
import com.github.javaparser.ast.type.VoidType;
import com.github.javaparser.ast.type.WildcardType;

import se.de.hu_berlin.informatik.astlmbuilder.nodes.UnknownNode;
import se.de.hu_berlin.informatik.astlmbuilder.parsing.InformationWrapper;

public interface INodeGuesser extends INodeGuesserBasics {

	//TODO: fill information wrapper with useful information on the way... (e.g. last seen nodes, etc.)

	default public ConstructorDeclaration guessConstructorDeclaration(InformationWrapper info) {
		info.addNodeClassToHistory(ConstructorDeclaration.class);
		// EnumSet<Modifier> modifiers
		// NodeList<AnnotationExpr> annotations
		// NodeList<TypeParameter> typeParameters 
		// SimpleName name
		// NodeList<Parameter> parameters
		// NodeList<ReferenceType> thrownExceptions
		// BlockStmt body
		return new ConstructorDeclaration(
				guessModifiers(info.getCopy()), 
				guessList(AnnotationExpr.class, info.getCopy()), 
				guessList(TypeParameter.class, info.getCopy()), 
				guessSimpleName(info.getCopy()), 
				guessList(Parameter.class, info.getCopy()), 
				guessList(ReferenceType.class, info.getCopy()), 
				guessBlockStmt(info.getCopy()));
	}

	public default InitializerDeclaration guessInitializerDeclaration(InformationWrapper info) {
		info.addNodeClassToHistory(InitializerDeclaration.class);
		// boolean isStatic
		// BlockStmt body
		return new InitializerDeclaration(
				guessBoolean(info.getCopy()), 
				guessBlockStmt(info.getCopy()));
	}

	public default EnumConstantDeclaration guessEnumConstantDeclaration(InformationWrapper info) {
		info.addNodeClassToHistory(EnumConstantDeclaration.class);
		// NodeList<AnnotationExpr> annotations,
		// SimpleName name
		// NodeList<Expression> arguments
		// NodeList<BodyDeclaration<?>> classBody
		return new EnumConstantDeclaration(
				guessList( AnnotationExpr.class, info.getCopy()),
				guessSimpleName(info.getCopy()),
				guessList( Expression.class, info.getCopy()),
				guessBodyDeclarationList( info.getCopy()));
	};

	public default VariableDeclarator guessVariableDeclarator(InformationWrapper info) {
		info.addNodeClassToHistory(VariableDeclarator.class);
		// Type type
		// SimpleName name
		// Expression initializer
		return new VariableDeclarator(
				guessNode( Type.class, info.getCopy() ),
				guessSimpleName(info.getCopy()), 
				guessNode( Expression.class, info.getCopy()));
	}

	public default EnumDeclaration guessEnumDeclaration(InformationWrapper info) {
		info.addNodeClassToHistory(EnumDeclaration.class);
		// EnumSet<Modifier> modifiers
		// NodeList<AnnotationExpr> annotations
		// SimpleName name
		// NodeList<ClassOrInterfaceType> implementedTypes
		// NodeList<EnumConstantDeclaration> entries
		// NodeList<BodyDeclaration<?>> members
		return new EnumDeclaration(
				guessModifiers(info.getCopy()), 
				guessList(AnnotationExpr.class, info.getCopy()), 
				guessSimpleName(info.getCopy()), 
				guessList(ClassOrInterfaceType.class, info.getCopy()), 
				guessList(EnumConstantDeclaration.class, info.getCopy()), 
				guessBodyDeclarationList( info.getCopy()));
	}

	public default AnnotationDeclaration guessAnnotationDeclaration(InformationWrapper info) throws IllegalArgumentException {
		info.addNodeClassToHistory(AnnotationDeclaration.class);
		// EnumSet<Modifier> modifiers
		// NodeList<AnnotationExpr> annotations
		// SimpleName name
		// NodeList<BodyDeclaration<?>> members
		return new AnnotationDeclaration(
				guessModifiers(info.getCopy()), 
				guessList(AnnotationExpr.class, info.getCopy()),
				guessSimpleName(info.getCopy()), 
				guessBodyDeclarationList( info.getCopy()));
	}

	public default AnnotationMemberDeclaration guessAnnotationMemberDeclaration(InformationWrapper info) throws IllegalArgumentException {
		info.addNodeClassToHistory(AnnotationMemberDeclaration.class);
		// EnumSet<Modifier> modifiers
		// NodeList<AnnotationExpr> annotations
		// Type type
		// SimpleName name
		// Expression defaultValue
		return new AnnotationMemberDeclaration(
				guessModifiers(info.getCopy()), 
				guessList(AnnotationExpr.class, info.getCopy()),
				guessNode( Type.class, info.getCopy() ), 
				guessSimpleName(info.getCopy()),
				guessNode( Expression.class, info.getCopy() ));
	}

	public default WhileStmt guessWhileStmt(InformationWrapper info) throws IllegalArgumentException {
		info.addNodeClassToHistory(WhileStmt.class);
		// final Expression condition
		// final Statement body
		return new WhileStmt(
				guessNode( Expression.class, info.getCopy() ), 
				guessBlockStmt(info.getCopy()));
	}

	public default TryStmt guessTryStmt(InformationWrapper info) throws IllegalArgumentException {
		info.addNodeClassToHistory(TryStmt.class);
		// NodeList<VariableDeclarationExpr> resources
		// final BlockStmt tryBlock
		// final NodeList<CatchClause> catchClauses
		// final BlockStmt finallyBlock
		return new TryStmt(
				guessList(VariableDeclarationExpr.class, info.getCopy()), 
				guessBlockStmt(info.getCopy()),
				guessList(CatchClause.class, info.getCopy()), 
				guessBlockStmt(info.getCopy()));
	}

	public default ThrowStmt guessThrowStmt(InformationWrapper info) throws IllegalArgumentException {
		info.addNodeClassToHistory(ThrowStmt.class);
		// final Expression expression
		return new ThrowStmt(
				guessNode( Expression.class, info.getCopy() ));
	}

	public default SynchronizedStmt guessSynchronizedStmt(InformationWrapper info) throws IllegalArgumentException {
		info.addNodeClassToHistory(SynchronizedStmt.class);
		// final Expression expression
		// final BlockStmt body
		return new SynchronizedStmt(
				guessNode( Expression.class, info.getCopy() ),
				guessBlockStmt(info.getCopy()));
	}

	public default SwitchStmt guessSwitchStmt(InformationWrapper info) throws IllegalArgumentException {
		info.addNodeClassToHistory(SwitchStmt.class);
		// final Expression selector
		// final NodeList<SwitchEntryStmt> entries
		return new SwitchStmt(
				guessNode( Expression.class, info.getCopy() ), 
				guessList(SwitchEntryStmt.class, info.getCopy()));
	}

	public default SwitchEntryStmt guessSwitchEntryStmt(InformationWrapper info) throws IllegalArgumentException {
		info.addNodeClassToHistory(SwitchEntryStmt.class);
		// final Expression label
		// final NodeList<Statement> statements
		return new SwitchEntryStmt(
				guessNode( Expression.class, info.getCopy() ), 
				guessList(Statement.class, info.getCopy()));
	}

	public default ReturnStmt guessReturnStmt(InformationWrapper info) throws IllegalArgumentException {
		info.addNodeClassToHistory(ReturnStmt.class);
		// final Expression expression
		return new ReturnStmt(
				guessNode( Expression.class, info.getCopy()));
	}

	public default LabeledStmt guessLabeledStmt(InformationWrapper info) throws IllegalArgumentException {
		info.addNodeClassToHistory(LabeledStmt.class);
		// final String label
		// final Statement statement
		return new LabeledStmt(
				guessStringValue(info.getCopy()), 
				guessNode( Statement.class, info.getCopy()));
	}

	public default IfStmt guessIfStmt(InformationWrapper info) throws IllegalArgumentException {
		info.addNodeClassToHistory(IfStmt.class);
		// final Expression condition
		// final Statement thenStmt
		// final Statement elseStmt
		return new IfStmt(
				guessNode( Expression.class, info.getCopy()),
				guessNode( Statement.class, info.getCopy()),
				guessNode( Statement.class, info.getCopy()));
	}


	public default ForStmt guessForStmt(InformationWrapper info) throws IllegalArgumentException {
		info.addNodeClassToHistory(ForStmt.class);
		// final NodeList<Expression> initialization
		// final Expression compare
		// final NodeList<Expression> update
		// final Statement body
		return new ForStmt(
				guessList(Expression.class, info.getCopy()),
				guessNode( Expression.class, info.getCopy()),
				guessList(Expression.class, info.getCopy()),
				guessNode( Statement.class, info.getCopy()));
	}

	public default ForeachStmt guessForeachStmt(InformationWrapper info) {
		info.addNodeClassToHistory(ForeachStmt.class);
		// final VariableDeclarationExpr variable
		// final Expression iterable
		// final Statement body
		return new ForeachStmt(
				guessVariableDeclarationExpr(info.getCopy()), 
				guessNode( Expression.class, info.getCopy()), 
				guessNode( Statement.class, info.getCopy()));
	}

	public default ExpressionStmt guessExpressionStmt(InformationWrapper info) {
		info.addNodeClassToHistory(ExpressionStmt.class);
		// final Expression expression
		return new ExpressionStmt(
				guessNode( Expression.class, info.getCopy()));
	}

	public default ExplicitConstructorInvocationStmt guessExplicitConstructorInvocationStmt(InformationWrapper info) {
		info.addNodeClassToHistory(ExplicitConstructorInvocationStmt.class);
		// final boolean isThis
		// final Expression expression
		// final NodeList<Expression> arguments
		return new ExplicitConstructorInvocationStmt(
				guessBoolean(info.getCopy()), 
				guessNode( Expression.class, info.getCopy()),
				guessList(Expression.class, info.getCopy()));
	}

	public default DoStmt guessDoStmt(InformationWrapper info) {
		info.addNodeClassToHistory(DoStmt.class);
		// final Statement body
		// final Expression condition
		return new DoStmt(
				guessNode( Statement.class, info.getCopy()),
				guessNode( Expression.class, info.getCopy()));
	}

	public default ContinueStmt guessContinueStmt(InformationWrapper info) {
		info.addNodeClassToHistory(ContinueStmt.class);
		// final SimpleName label
		return new ContinueStmt(
				guessSimpleName(info.getCopy()));
	}

	public default CatchClause guessCatchClause(InformationWrapper info) {
		info.addNodeClassToHistory(CatchClause.class);
		// final EnumSet<Modifier> exceptModifier
		// final NodeList<AnnotationExpr> exceptAnnotations
		// final ClassOrInterfaceType exceptType
		// final SimpleName exceptName
		// final BlockStmt body
		return new CatchClause(
				guessModifiers(info.getCopy()), 
				guessList(AnnotationExpr.class, info.getCopy()), 
				guessClassOrInterfaceType( info.getCopy()), 
				guessSimpleName(info.getCopy()), 
				guessBlockStmt(info.getCopy()));
	}

	public default BlockStmt guessBlockStmt(InformationWrapper info) {
		info.addNodeClassToHistory(BlockStmt.class);
		// final NodeList<Statement> statements
		return new BlockStmt( 
				guessList(Statement.class, info.getCopy()));
	}

	public default VariableDeclarationExpr guessVariableDeclarationExpr(InformationWrapper info) {
		info.addNodeClassToHistory(VariableDeclarationExpr.class);
		// final EnumSet<Modifier> modifiers
		// final NodeList<AnnotationExpr> annotations
		// final NodeList<VariableDeclarator> variables
		return new VariableDeclarationExpr(
				guessModifiers(info.getCopy()), 
				guessList(AnnotationExpr.class, info.getCopy()), 
				guessList(VariableDeclarator.class, info.getCopy()));
	}

	public default TypeExpr guessTypeExpr(InformationWrapper info) {
		info.addNodeClassToHistory(TypeExpr.class);
		// Type type
		return new TypeExpr(
				guessNode( Type.class,info.getCopy() ));
	}

	public default SuperExpr guessSuperExpr(InformationWrapper info) {
		info.addNodeClassToHistory(SuperExpr.class);
		// final Expression classExpr
		return new SuperExpr(
				guessNode( Expression.class, info.getCopy() ));
	}

	public default NullLiteralExpr guessNullLiteralExpr(InformationWrapper info) {
		info.addNodeClassToHistory(NullLiteralExpr.class);
		// funny :D
		return new NullLiteralExpr();
	}

	public default MethodReferenceExpr guessMethodReferenceExpr(InformationWrapper info) {
		info.addNodeClassToHistory(MethodReferenceExpr.class);
		// Expression scope
		// NodeList<Type> typeArguments
		// String identifier
		return new MethodReferenceExpr(
				guessNode( Expression.class, info.getCopy()), 
				guessList(Type.class, info.getCopy()),
				guessMethodIdentifier( info.getCopy()));
	}

	public default LambdaExpr guessLambdaExpr(InformationWrapper info) {
		info.addNodeClassToHistory(LambdaExpr.class);
		// NodeList<Parameter> parameters
		// Statement body
		// boolean isEnclosingParameters
		return new LambdaExpr(
				guessList(Parameter.class, info.getCopy()), 
				guessNode( Statement.class, info.getCopy() ),
				guessBoolean(info.getCopy()));
	}

	public default InstanceOfExpr guessInstanceOfExpr(InformationWrapper info) {
		info.addNodeClassToHistory(InstanceOfExpr.class);
		// final Expression expression
		// final ReferenceType<?> type
		return new InstanceOfExpr(
				guessNode( Expression.class, info.getCopy() ), 
				guessNode( ReferenceType.class, info.getCopy()));
	}

	public default FieldAccessExpr guessFieldAccessExpr(InformationWrapper info) {
		info.addNodeClassToHistory(FieldAccessExpr.class);
		// final Expression scope
		// final NodeList<Type> typeArguments
		// final SimpleName name
		return new FieldAccessExpr(
				guessNode( Expression.class, info.getCopy()), 
				guessList(Type.class, info.getCopy()), 
				guessSimpleName(info.getCopy()));
	}

	public default ConditionalExpr guessConditionalExpr(InformationWrapper info) {
		info.addNodeClassToHistory(ConditionalExpr.class);
		// Expression condition
		// Expression thenExpr
		// Expression elseExpr
		return new ConditionalExpr(
				guessNode( Expression.class, info.getCopy()), 
				guessNode( Expression.class, info.getCopy()), 
				guessNode( Expression.class, info.getCopy()));
	}

	public default ClassExpr guessClassExpr(InformationWrapper info) {
		info.addNodeClassToHistory(ClassExpr.class);
		// Type type
		return new ClassExpr(
				guessNode( Type.class,info.getCopy()));
	}

	public default CastExpr guessCastExpr(InformationWrapper info) {
		info.addNodeClassToHistory(CastExpr.class);
		// Type type
		// Expression expression
		return new CastExpr(
				guessNode( Type.class,info.getCopy()),
				guessNode( Expression.class, info.getCopy()));
	}

	public default AssignExpr guessAssignExpr(InformationWrapper info) {
		info.addNodeClassToHistory(AssignExpr.class);
		// Expression target
		// Expression value
		// Operator operator
		return new AssignExpr(
				guessNode( Expression.class, info.getCopy()),
				guessNode( Expression.class, info.getCopy()),
				guessAssignOperator(info.getCopy()));
	}

	public default ArrayInitializerExpr guessArrayInitializerExpr(InformationWrapper info) {
		info.addNodeClassToHistory(ArrayInitializerExpr.class);
		// NodeList<Expression> values
		return new ArrayInitializerExpr(
				guessList(Expression.class, info.getCopy()));
	}

	public default ArrayCreationExpr guessArrayCreationExpr(InformationWrapper info) {
		info.addNodeClassToHistory(ArrayCreationExpr.class);
		// Type elementType
		// NodeList<ArrayCreationLevel> levels
		// ArrayInitializerExpr initializer
		return new ArrayCreationExpr(
				guessNode( Type.class, info.getCopy()), 
				guessList(ArrayCreationLevel.class, info.getCopy()), 
				guessArrayInitializerExpr( info.getCopy()));
	}

	public default ArrayAccessExpr guessArrayAccessExpr(InformationWrapper info) {
		info.addNodeClassToHistory(ArrayAccessExpr.class);
		// Expression name
		// Expression index
		return new ArrayAccessExpr(
				guessNode( Expression.class, info.getCopy()),
				guessNode( Expression.class, info.getCopy()));
	}

	public default PackageDeclaration guessPackageDeclaration(InformationWrapper info) {
		info.addNodeClassToHistory(PackageDeclaration.class);
		// NodeList<AnnotationExpr> annotations
		// Name name
		return new PackageDeclaration(
				guessList(AnnotationExpr.class, info.getCopy()), 
				guessName(info.getCopy()));
	}

	public default ImportDeclaration guessImportDeclaration(InformationWrapper info) {
		info.addNodeClassToHistory(ImportDeclaration.class);
		// Name name
		// boolean isStatic
		// boolean isAsterisk
		return new ImportDeclaration(
				guessName(info.getCopy()),
				guessBoolean(info.getCopy()),
				guessBoolean(info.getCopy()));
	}

	public default FieldDeclaration guessFieldDeclaration(InformationWrapper info) {
		info.addNodeClassToHistory(FieldDeclaration.class);
		// EnumSet<Modifier> modifiers
		// NodeList<AnnotationExpr> annotations
		// NodeList<VariableDeclarator> variables
		return new FieldDeclaration(
				guessModifiers(info.getCopy()), 
				guessList(AnnotationExpr.class, info.getCopy()), 
				guessList(VariableDeclarator.class, info.getCopy()));
	}

	public default ClassOrInterfaceType guessClassOrInterfaceType(InformationWrapper info) {
		info.addNodeClassToHistory(ClassOrInterfaceType.class);
		// final ClassOrInterfaceType scope
		// final SimpleName name
		// final NodeList<Type> typeArguments
		return new ClassOrInterfaceType(
				guessClassOrInterfaceType(info.getCopy()), 
				guessSimpleName(info.getCopy()), 
				guessList(Type.class, info.getCopy()));
	}

	public default ClassOrInterfaceDeclaration guessClassOrInterfaceDeclaration(InformationWrapper info) {
		info.addNodeClassToHistory(ClassOrInterfaceDeclaration.class);
		// final EnumSet<Modifier> modifiers
		// final NodeList<AnnotationExpr> annotations
		// final boolean isInterface
		// final SimpleName name
		// final NodeList<TypeParameter> typeParameters
		// final NodeList<ClassOrInterfaceType> extendedTypes
		// final NodeList<ClassOrInterfaceType> implementedTypes
		// final NodeList<BodyDeclaration<?>> members
		return new ClassOrInterfaceDeclaration(
				guessModifiers(info.getCopy()), 
				guessList(AnnotationExpr.class, info.getCopy()), 
				guessBoolean(info.getCopy()), 
				guessSimpleName(info.getCopy()), 
				guessList(TypeParameter.class, info.getCopy()), 
				guessList(ClassOrInterfaceType.class, info.getCopy()),
				guessList(ClassOrInterfaceType.class, info.getCopy()),
				guessBodyDeclarationList(info.getCopy()));
	}

	public default MethodDeclaration guessMethodDeclaration(InformationWrapper info) {
		info.addNodeClassToHistory(MethodDeclaration.class);
		// final EnumSet<Modifier> modifiers
		// final NodeList<AnnotationExpr> annotations
		// final NodeList<TypeParameter> typeParameters
		// final Type type
		// final SimpleName name
		// final boolean isDefault
		// final NodeList<Parameter> parameters
		// final NodeList<ReferenceType> thrownExceptions
		// final BlockStmt body
		return new MethodDeclaration(
				guessModifiers(info.getCopy()), 
				guessList(AnnotationExpr.class, info.getCopy()), 
				guessList(TypeParameter.class, info.getCopy()),
				guessNode( Type.class, info.getCopy()),
				guessSimpleName(info.getCopy()),
				guessBoolean(info.getCopy()),
				guessList(Parameter.class, info.getCopy()), 
				guessList(ReferenceType.class, info.getCopy()), 
				guessBlockStmt(info.getCopy()));
	}

	public default BinaryExpr guessBinaryExpr(InformationWrapper info) {
		info.addNodeClassToHistory(BinaryExpr.class);
		// Expression left
		// Expression right
		// Operator operator
		return new BinaryExpr(
				guessNode( Expression.class, info.getCopy()),
				guessNode( Expression.class, info.getCopy()),
				guessBinaryOperator(info.getCopy()));
	}

	public default UnaryExpr guessUnaryExpr(InformationWrapper info) {
		info.addNodeClassToHistory(UnaryExpr.class);
		// final Expression expression
		// final Operator operator
		return new UnaryExpr(
				guessNode( Expression.class, info.getCopy()),
				guessUnaryOperator(info.getCopy()));
	}

	public default MethodCallExpr guessMethodCallExpr(InformationWrapper info) {
		info.addNodeClassToHistory(MethodCallExpr.class);
		// final Expression scope
		// final NodeList<Type> typeArguments
		// final SimpleName name
		// final NodeList<Expression> arguments
		return new MethodCallExpr(
				guessNode( Expression.class, info.getCopy() ), 
				guessList(Type.class, info.getCopy()), 
				guessSimpleName(info.getCopy()), 
				guessList(Expression.class, info.getCopy()));
	}

	public default NameExpr guessNameExpr(InformationWrapper info) {
		info.addNodeClassToHistory(NameExpr.class);
		// final SimpleName name
		return new NameExpr(
				guessSimpleName(info.getCopy()));
	}

	public default ConstructorDeclaration guessIntegerLiteralExpr(InformationWrapper info) {
		info.addNodeClassToHistory(ConstructorDeclaration.class);
		// EnumSet<Modifier> modifiers
		// NodeList<AnnotationExpr> annotations
		// NodeList<TypeParameter> typeParameters
		// SimpleName name
		// NodeList<Parameter> parameters
		// NodeList<ReferenceType> thrownExceptions
		// BlockStmt body
		return new ConstructorDeclaration(
				guessModifiers(info.getCopy()), 
				guessList(AnnotationExpr.class, info.getCopy()), 
				guessList(TypeParameter.class, info.getCopy()), 
				guessSimpleName(info.getCopy()), 
				guessList(Parameter.class, info.getCopy()), 
				guessList(ReferenceType.class, info.getCopy()), 
				guessBlockStmt(info.getCopy()));
	}

	public default DoubleLiteralExpr guessDoubleLiteralExpr(InformationWrapper info) {
		info.addNodeClassToHistory(DoubleLiteralExpr.class);
		// final String value
		return new DoubleLiteralExpr(
				guessStringValue(info.getCopy()));
	}

	public default StringLiteralExpr guessStringLiteralExpr(InformationWrapper info) {
		info.addNodeClassToHistory(StringLiteralExpr.class);
		// final String value
		return new StringLiteralExpr(
				guessStringValue(info.getCopy()));
	}

	public default BooleanLiteralExpr guessBooleanLiteralExpr(InformationWrapper info) {
		info.addNodeClassToHistory(BooleanLiteralExpr.class);
		// boolean value
		return new BooleanLiteralExpr(
				guessBoolean(info.getCopy()));
	}

	public default CharLiteralExpr guessCharLiteralExpr(InformationWrapper info) {
		info.addNodeClassToHistory(CharLiteralExpr.class);
		// String value
		return new CharLiteralExpr(
				guessStringValue(info.getCopy()));
	}

	public default LongLiteralExpr guessLongLiteralExpr(InformationWrapper info) {
		info.addNodeClassToHistory(LongLiteralExpr.class);
		// final String value
		return new LongLiteralExpr(
				guessStringValue(info.getCopy()));
	}

	public default ThisExpr guessThisExpr(InformationWrapper info) {
		info.addNodeClassToHistory(ThisExpr.class);
		// final Expression classExpr
		return new ThisExpr(
				guessNode( Expression.class, info.getCopy() ));
	}

	public default BreakStmt guessBreakStmt(InformationWrapper info) {
		info.addNodeClassToHistory(BreakStmt.class);
		// final SimpleName label
		return new BreakStmt(
				guessSimpleName(info.getCopy()));
	}

	public default ObjectCreationExpr guessObjectCreationExpr(InformationWrapper info) {
		info.addNodeClassToHistory(ObjectCreationExpr.class);
		// final Expression scope
		// final ClassOrInterfaceType type
		// final NodeList<Type> typeArguments
		// final NodeList<Expression> arguments
		// final NodeList<BodyDeclaration<?>> anonymousClassBody
		return new ObjectCreationExpr(
				guessNode( Expression.class, info.getCopy()), 
				guessClassOrInterfaceType( info.getCopy()), 
				guessList(Type.class, info.getCopy()), 
				guessList(Expression.class, info.getCopy()), 
				guessBodyDeclarationList(info.getCopy()));
	}

	public default MarkerAnnotationExpr guessMarkerAnnotationExpr(InformationWrapper info) {
		info.addNodeClassToHistory(MarkerAnnotationExpr.class);
		// final Name name
		return new MarkerAnnotationExpr( 
				guessName(info.getCopy())); 
	}

	public default NormalAnnotationExpr guessNormalAnnotationExpr(InformationWrapper info) {
		info.addNodeClassToHistory(NormalAnnotationExpr.class);
		// final Name name
		// final NodeList<MemberValuePair> pairs
		return new NormalAnnotationExpr(
				guessName(info.getCopy()),
				guessList(MemberValuePair.class, info.getCopy()));
	}

	public default SingleMemberAnnotationExpr guessSingleMemberAnnotationExpr(InformationWrapper info) {
		info.addNodeClassToHistory(SingleMemberAnnotationExpr.class);
		// final Name name
		// final Expression memberValue
		return new SingleMemberAnnotationExpr(
				guessName(info.getCopy()),
				guessNode( Expression.class, info.getCopy()));
	}

	public default Parameter guessParameter(InformationWrapper info) {
		info.addNodeClassToHistory(Parameter.class);
		// EnumSet<Modifier> modifiers
		// NodeList<AnnotationExpr> annotations
		// Type type
		// boolean isVarArgs
		// NodeList<AnnotationExpr> varArgsAnnotations
		// SimpleName name
		return new Parameter(
				guessModifiers(info.getCopy()), 
				guessList(AnnotationExpr.class, info.getCopy()), 
				guessNode( Type.class, info.getCopy()),
				guessBoolean(info.getCopy()),
				guessList(AnnotationExpr.class, info.getCopy()), 
				guessSimpleName(info.getCopy()));
	}

	public default EnclosedExpr guessEnclosedExpr(InformationWrapper info) {
		info.addNodeClassToHistory(EnclosedExpr.class);
		// final Expression inner
		return new EnclosedExpr(
				guessNode( Expression.class, info.getCopy()));
	}

	public default AssertStmt guessAssertStmt(InformationWrapper info) {
		info.addNodeClassToHistory(AssertStmt.class);
		// final Expression check
		// final Expression message
		return new AssertStmt(
				guessNode( Expression.class, info.getCopy()),
				guessNode( Expression.class, info.getCopy()));
	}

	public default ConstructorDeclaration guessMemberValuePair(InformationWrapper info) {
		info.addNodeClassToHistory(ConstructorDeclaration.class);
		// EnumSet<Modifier> modifiers
		// NodeList<AnnotationExpr> annotations
		// NodeList<TypeParameter> typeParameters
		// SimpleName name
		// NodeList<Parameter> parameters
		// NodeList<ReferenceType> thrownExceptions
		// BlockStmt body
		return new ConstructorDeclaration(
				guessModifiers(info.getCopy()), 
				guessList(AnnotationExpr.class, info.getCopy()), 
				guessList(TypeParameter.class, info.getCopy()), 
				guessSimpleName(info.getCopy()), 
				guessList(Parameter.class, info.getCopy()), 
				guessList(ReferenceType.class, info.getCopy()), 
				guessBlockStmt(info.getCopy()));
	}

	public default PrimitiveType guessPrimitiveType(InformationWrapper info) {
		info.addNodeClassToHistory(PrimitiveType.class);
		// final Primitive type
		return new PrimitiveType(
				guessPrimitive(info.getCopy()));
	}

	// this may never be used
	public default UnionType guessUnionType(InformationWrapper info) {
		info.addNodeClassToHistory(UnionType.class);
		// NodeList<ReferenceType> elements
		return new UnionType(
				guessList(ReferenceType.class, info.getCopy()));
	}

	public default IntersectionType guessIntersectionType(InformationWrapper info) {
		info.addNodeClassToHistory(IntersectionType.class);
		// NodeList<ReferenceType> elements
		return new IntersectionType( 
				guessList(ReferenceType.class, info.getCopy()));
	}

	public default TypeParameter guessTypeParameter(InformationWrapper info) {
		info.addNodeClassToHistory(TypeParameter.class);
		// SimpleName name
		// NodeList<ClassOrInterfaceType> typeBound
		// NodeList<AnnotationExpr> annotations
		return new TypeParameter( 
				guessSimpleName(info.getCopy()), 
				guessList(ClassOrInterfaceType.class, info.getCopy()), 
				guessList(AnnotationExpr.class, info.getCopy()));
	}

	public default WildcardType guessWildcardType(InformationWrapper info) {
		info.addNodeClassToHistory(WildcardType.class);
		// final ReferenceType extendedType
		// final ReferenceType superType
		return new WildcardType(
				guessNode( ReferenceType.class, info.getCopy() ),
				guessNode( ReferenceType.class, info.getCopy() ));
	}

	public default VoidType guessVoidType(InformationWrapper info) {
		info.addNodeClassToHistory(VoidType.class);
		return new VoidType();
	}

	public default UnknownType guessUnknownType(InformationWrapper info) {
		info.addNodeClassToHistory(UnknownType.class);
		// none
		return new UnknownType();
	}

	// only needed for debugging
	public default UnknownNode guessUnknown(InformationWrapper info) {
		info.addNodeClassToHistory(UnknownNode.class);	
		// none
		return new UnknownNode();
	}

	public default Name guessName(InformationWrapper info) {
		info.addNodeClassToHistory(Name.class);
		// final String identifier
		// NodeList<AnnotationExpr> annotations
		return new Name(
				guessName( info.getCopy() ), // this will return null eventually but is this a bug or a feature?
				guessStringValue(info.getCopy()),
				guessList(AnnotationExpr.class, info.getCopy()));
	}

	public default SimpleName guessSimpleName(InformationWrapper info) {
		info.addNodeClassToHistory(SimpleName.class);
		// final String identifier
		return new SimpleName(
				guessStringValue(info.getCopy()));
	}

	public default LocalClassDeclarationStmt guessLocalClassDeclarationStmt(InformationWrapper info) {
		info.addNodeClassToHistory(LocalClassDeclarationStmt.class);
		// final ClassOrInterfaceDeclaration classDeclaration
		return new LocalClassDeclarationStmt(
				guessClassOrInterfaceDeclaration( info.getCopy()));
	}
	public default ArrayType guessArrayType(InformationWrapper info) {
		info.addNodeClassToHistory(ArrayType.class);
		// Type componentType
		// NodeList<AnnotationExpr> annotations
		return new ArrayType(
				guessNode( Type.class, info.getCopy()), 
				guessList(AnnotationExpr.class, info.getCopy()));
	}

	public default ArrayCreationLevel guessArrayCreationLevel(InformationWrapper info) {
		info.addNodeClassToHistory(ArrayCreationLevel.class);
		// Expression dimension
		// NodeList<AnnotationExpr> annotations
		return new ArrayCreationLevel(
				guessNode( Expression.class, info.getCopy()), 
				guessList(AnnotationExpr.class, info.getCopy()));
	}

	public default ModuleDeclaration guessModuleDeclaration(InformationWrapper info) {
		info.addNodeClassToHistory(ModuleDeclaration.class);
		// NodeList<AnnotationExpr> annotations
		// Name name
		// boolean isOpen
		// NodeList<ModuleStmt> moduleStmts
		return new ModuleDeclaration( 
				guessList(AnnotationExpr.class, info.getCopy()), 
				guessName(info.getCopy()),
				guessBoolean(info.getCopy()),
				guessList(ModuleStmt.class, info.getCopy()));
	}

	public default ModuleExportsStmt guessModuleExportsStmt(InformationWrapper info) {
		info.addNodeClassToHistory(ModuleExportsStmt.class);
		// Name name
		// NodeList<Name> moduleNames
		return new ModuleExportsStmt( 
				guessName(info.getCopy()),
				guessList(Name.class, info.getCopy()));
	}

	public default ModuleOpensStmt guessModuleOpensStmt(InformationWrapper info) {
		info.addNodeClassToHistory(ModuleOpensStmt.class);
		// Name name
		// NodeList<Name> moduleNames
		return new ModuleOpensStmt( 
				guessName(info.getCopy()),
				guessList(Name.class, info.getCopy()));
	}

	public default ModuleProvidesStmt guessModuleProvidesStmt(InformationWrapper info) {
		info.addNodeClassToHistory(ModuleProvidesStmt.class);
		// Type type
		// NodeList<Type> withTypes
		return new ModuleProvidesStmt( 
				guessNode( Type.class, info.getCopy()),
				guessList(Type.class, info.getCopy()));
	}

	public default ModuleRequiresStmt guessModuleRequiresStmt(InformationWrapper info) {
		info.addNodeClassToHistory(ModuleRequiresStmt.class);
		// EnumSet<Modifier> modifiers
		// Name name
		return new ModuleRequiresStmt( 
				guessModifiers(info.getCopy()), 
				guessName(info.getCopy()));
	}

	public default ModuleUsesStmt guessModuleUsesStmt(InformationWrapper info) {
		info.addNodeClassToHistory(ModuleUsesStmt.class);
		// Type type
		return new ModuleUsesStmt( 
				guessNode( Type.class, info.getCopy()));
	}

	public default CompilationUnit guessCompilationUnit(InformationWrapper info) {
		info.addNodeClassToHistory(CompilationUnit.class);
		// PackageDeclaration packageDeclaration
		// NodeList<ImportDeclaration> imports
		// NodeList<TypeDeclaration<?>> types
		// ModuleDeclaration module
		return new CompilationUnit( 
				guessNode( PackageDeclaration.class, info.getCopy()),
				guessList(ImportDeclaration.class, info.getCopy()),
				guessTypeDeclarationList(info.getCopy()),
				guessNode( ModuleDeclaration.class, info.getCopy()));
	}


}