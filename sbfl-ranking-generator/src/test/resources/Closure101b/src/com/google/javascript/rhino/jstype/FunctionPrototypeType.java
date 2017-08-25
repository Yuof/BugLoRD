/*
 *
 * ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Rhino code, released
 * May 6, 1999.
 *
 * The Initial Developer of the Original Code is
 * Netscape Communications Corporation.
 * Portions created by the Initial Developer are Copyright (C) 1997-1999
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Bob Jervis
 *   Google Inc.
 *
 * Alternatively, the contents of this file may be used under the terms of
 * the GNU General Public License Version 2 or later (the "GPL"), in which
 * case the provisions of the GPL are applicable instead of those above. If
 * you wish to allow use of your version of this file only under the terms of
 * the GPL and not to allow others to use your version of this file under the
 * MPL, indicate your decision by deleting the provisions above and replacing
 * them with the notice and other provisions required by the GPL. If you do
 * not delete the provisions above, a recipient may use your version of this
 * file under either the MPL or the GPL.
 *
 * ***** END LICENSE BLOCK ***** */

package com.google.javascript.rhino.jstype;

import com.google.javascript.rhino.ErrorReporter;
import com.google.javascript.rhino.jstype.ObjectType;

import java.util.List;
import java.util.Set;

/**
 * Represents the prototype of a {@link FunctionType}.
 * @author nicksantos@google.com (Nick Santos)
 */
public class FunctionPrototypeType extends PrototypeObjectType {
  private static final long serialVersionUID = 1L;

  private final FunctionType ownerFunction;

  FunctionPrototypeType(JSTypeRegistry registry, FunctionType ownerFunction,
      ObjectType implicitPrototype, boolean isNative) {
    super(registry, null /* has no class name */, implicitPrototype,
        isNative);
    this.ownerFunction = ownerFunction;
  }

  FunctionPrototypeType(JSTypeRegistry registry, FunctionType ownerFunction,
      ObjectType implicitPrototype) {
    this(registry, ownerFunction, implicitPrototype, false);
  }

  @Override
  public String getReferenceName() {
    if (ownerFunction == null) {
      return "{...}.prototype";
    } else {
      return ownerFunction.getReferenceName() + ".prototype";
    }
  }

  @Override
  public boolean hasReferenceName() {
    return ownerFunction != null && ownerFunction.hasReferenceName();
  }

  @Override
  public boolean isFunctionPrototypeType() {
    return true;
  }

  public FunctionType getOwnerFunction() {
    return ownerFunction;
  }

  @Override
  public Iterable<ObjectType> getCtorImplementedInterfaces() {
    return getOwnerFunction().getImplementedInterfaces();
  }

  // The owner will always be a resolved type, so there's no need to set
  // the ownerFunction in resolveInternal.
  // (it would lead to infinite loops if we did).
  // JSType resolveInternal(ErrorReporter t, StaticScope<JSType> scope);
}
