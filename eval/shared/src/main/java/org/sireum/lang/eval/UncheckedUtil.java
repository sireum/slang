/*
 Copyright (c) 2017-2024, Robby, Kansas State University
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

 1. Redistributions of source code must retain the above copyright notice, this
    list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.sireum.lang.eval;

import org.sireum.Z;

@SuppressWarnings({"rawtypes", "unchecked"})
public class UncheckedUtil {
    public static Object binary(Object l, org.sireum.String op, Object r) {
        Z.BV left = (Z.BV) l;
        Z.BV right = (Z.BV) r;
        return switch (op.value()) {
            case "+" -> left.$plus(right);
            case "-" -> left.$minus(right);
            case "*" -> left.$times(right);
            case "/" -> left.$div(right);
            case "%" -> left.$percent(right);
            case "≡" -> left.equals(right);
            case "≢" -> !left.equals(right);
            case "<" -> left.$less(right);
            case ">" -> left.$greater(right);
            case "<=" -> left.$less$eq(right);
            case ">=" -> left.$greater$eq(right);
            case ">>" -> left.$greater$greater(right);
            case ">>>" -> left.$greater$greater$greater(right);
            case "<<" -> left.$less$less(right);
            case "&" -> left.$amp(right);
            case "|" -> left.$bar(right);
            case "|^" -> left.$bar$up(right);
            default -> null;
        };
    }
}
