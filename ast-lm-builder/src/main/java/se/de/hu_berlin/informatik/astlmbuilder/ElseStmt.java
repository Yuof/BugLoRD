package se.de.hu_berlin.informatik.astlmbuilder;

import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;

public class ElseStmt extends Statement {

	@SuppressWarnings("deprecation")
	public ElseStmt(final int beginLine, final int beginColumn, 
			final int endLine, final int endColumn) {
		super(beginLine, beginColumn, endLine, endColumn);
	}
	
	// simple constructor for the deserialization
	public ElseStmt() {
		super();
	}
	
	@Override
	public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <A> void accept(VoidVisitor<A> v, A arg) {
		// TODO Auto-generated method stub
		
	}

}
