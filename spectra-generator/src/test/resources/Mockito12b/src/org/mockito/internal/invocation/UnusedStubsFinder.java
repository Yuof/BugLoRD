/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.invocation;

import org.mockito.internal.MockHandlerInterface;
import org.mockito.internal.stubbing.StubbedInvocationMatcher;
import org.mockito.internal.util.MockUtil;

import java.util.LinkedList;
import java.util.List;

public class UnusedStubsFinder {

    /**
     * Finds all unused stubs for given mocks
     *
     * @param mocks
     * @return
     */
    public List<Invocation> find(List<?> mocks) {
        List<Invocation> unused = new LinkedList<Invocation>();
        for (Object mock : mocks) {
            MockHandlerInterface<Object> handler = new MockUtil().getMockHandler(mock);
            List<StubbedInvocationMatcher> fromSingleMock = handler.getInvocationContainer().getStubbedInvocations();
            for (StubbedInvocationMatcher s : fromSingleMock) {
                if (!s.wasUsed()) {
                    unused.add(s.getInvocation());
                }
            }
        }
        return unused;
    }
}