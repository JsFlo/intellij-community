/*
 * Copyright 2003-2007 Dave Griffith, Bas Leijdekkers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.siyeh.ig.threading;

import com.intellij.psi.*;
import com.siyeh.ig.psiutils.ClassUtils;

public class ThreadingUtils {

    private ThreadingUtils() {
        super();
    }

    public static boolean isWaitCall(PsiMethodCallExpression expression) {
        final PsiReferenceExpression methodExpression = expression.getMethodExpression();
        final String methodName = methodExpression.getReferenceName();
        if (!"wait".equals(methodName)) {
            return false;
        }
        final PsiMethod method = expression.resolveMethod();
        if (method == null) {
            return false;
        }
        final PsiParameterList parameterList = method.getParameterList();
        final int numParams = parameterList.getParametersCount();
        if (numParams > 2) {
            return false;
        }
        final PsiParameter[] parameters = parameterList.getParameters();
        if (numParams > 0) {
            final PsiType parameterType = parameters[0].getType();
            if (!parameterType.equals(PsiType.LONG)) {
                return false;
            }
        }
        if (numParams > 1) {
            final PsiType parameterType = parameters[1].getType();
            if (!parameterType.equals(PsiType.INT)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotifyOrNotifyAllCall(PsiMethodCallExpression expression) {
        final PsiReferenceExpression methodExpression = expression.getMethodExpression();

        final String methodName = methodExpression.getReferenceName();
        if (!"notify".equals(methodName) && !"notifyAll".equals(methodName)) {
            return false;
        }
        final PsiExpressionList argumentList = expression.getArgumentList();

        final PsiExpression[] args = argumentList.getExpressions();
        return args.length == 0;
    }

    public static boolean isSignalOrSignalAllCall(PsiMethodCallExpression expression) {
        final PsiReferenceExpression methodExpression = expression.getMethodExpression();
        final String methodName = methodExpression.getReferenceName();
        if (!"signal".equals(methodName) && !"signalAll".equals(methodName)) {
            return false;
        }
        final PsiExpressionList argumentList = expression.getArgumentList();
        final PsiExpression[] args = argumentList.getExpressions();
        if (args.length != 0) {
            return false;
        }
        final PsiMethod method = expression.resolveMethod();
        if (method == null) {
            return false;
        }
        final PsiClass containingClass = method.getContainingClass();
        if (containingClass == null) {
            return false;
        }
        return ClassUtils.isSubclass(containingClass,
                        "java.util.concurrent.locks.Condition");

    }
    public static boolean isAwaitCall(PsiMethodCallExpression expression) {
        final PsiReferenceExpression methodExpression = expression.getMethodExpression();
        final String methodName = methodExpression.getReferenceName();
        if (!"await".equals(methodName)
                && !"awaitUntil".equals(methodName)
                && !"awaitUninterruptibly".equals(methodName)
                && !"awaitNanos".equals(methodName)) {
            return false;
        }
        final PsiMethod method = expression.resolveMethod();
        if (method == null) {
            return false;
        }
        final PsiClass containingClass = method.getContainingClass();
        if (containingClass == null) {
            return false;
        }
        return ClassUtils.isSubclass(containingClass,
                        "java.util.concurrent.locks.Condition");

    }
}
