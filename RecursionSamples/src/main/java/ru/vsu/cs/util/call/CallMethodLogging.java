package ru.vsu.cs.util.call;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class CallMethodLogging {

    public static class MethodCall implements Iterable<MethodCall> {

        private String name = null;
        private List params = null;
        private String prefix = null;
        private String suffix = null;
        private MethodCall parent = null;
        private boolean resultSetted = false;
        private Object result = null;

        private List<MethodCall> methodCalls = new ArrayList<>();

        public MethodCall(String methodName, List methodParams,
                String prefix, String suffix, MethodCall parent) {
            this.name = methodName;
            this.prefix = prefix;
            this.suffix = suffix;
            this.params = methodParams == null ? new ArrayList() : methodParams;
            this.parent = parent;
        }

        public MethodCall(String methodName, List methodParams) {
            this(methodName, methodParams, null, null, null);
        }

        public MethodCall(String methodName) {
            this(methodName, null);
        }

        public void add(MethodCall methodCall) {
            methodCalls.add(methodCall);
        }

        public int getMethodCallsCount() {
            return methodCalls.size();
        }

        @Override
        public Iterator<MethodCall> iterator() {
            return new Iterator<MethodCall>() {
                private int currentIndex = 0;

                @Override
                public boolean hasNext() {
                    return currentIndex < getMethodCallsCount();
                }

                @Override
                public MethodCall next() {
                    return methodCalls.get(currentIndex++);
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }

        private static void toStringInner(StringBuilder sb, List list, boolean inner) {
            if (list == null) {
                return;
            }
            if (inner) {
                sb.append("{ ");
            }
            for (int i = 0; i < list.size(); i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                if (list.get(i) == null) {
                    sb.append("null");
                } else if (list.get(i) instanceof String) {
                    sb.append(String.format("\"%s\"", list.get(i)));
                } else if (list.get(i) instanceof Character) {
                    sb.append(String.format("'%s'", list.get(i)));
                } else if (list.get(i) instanceof List) {
                    toStringInner(sb, (List) list.get(i), true);
                } else {
                    sb.append(list.get(i).toString());
                }
            }
            if (inner) {
                sb.append(" }");
            }
        }


        public static final String
            //ARROW_STRING     = "->";
            ARROW_STRING     = "\u2192";

        private void toStringInner(StringBuilder sb) {
            if (getPrefix() != null) {
                sb.append(getPrefix());
            }
            sb.append((getName() == null) ? "..." : getName());
            if (getName() == null && (params == null || params.size() == 0) && !isResultSetted()) {
                return;
            }
            sb.append("(");
            toStringInner(sb, params, false);
            sb.append(")");
            if (getSuffix() != null) {
                sb.append(getSuffix());
            }
            if (isResultSetted()) {
                sb.append(" ").append(ARROW_STRING).append(" ");
                if (result instanceof List) {
                    toStringInner(sb, (List) result, true);
                } else {
                    toStringInner(sb, Arrays.asList(result), false);
                }
            }
        }


        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            toStringInner(sb);
            return sb.toString();
        }


        public static final char
            CONNECT_CHAR     = '\u2502',
            MIDDLE_NODE_CHAR = '\u251C',
            LAST_NODE_CHAR   = '\u2514';

        private static void asTreeInner(StringBuilder sb, MethodCall call, int callIndex, String indent, boolean root) {
            if (call == null) {
                return;
            }
            sb.append(indent);
            if (!root) {
                if (callIndex < call.getParent().methodCalls.size() - 1) {
                    sb.append(MIDDLE_NODE_CHAR).append(" ");
                    indent += CONNECT_CHAR + " ";
                } else {
                    sb.append(LAST_NODE_CHAR).append(" ");
                    indent += "  ";
                }
            }
            call.toStringInner(sb);
            sb.append(System.lineSeparator());
            int i = 0;
            for (MethodCall child : call) {
                asTreeInner(sb, child, i++, indent, false);
            }
        }

        public String asTree() {
            StringBuilder sb = new StringBuilder();
            if (this.getName() == null) {
                for (MethodCall call : this)
                    asTreeInner(sb, call, 0, "", true);
            }
            else {
                asTreeInner(sb, this, 0, "", true);
            }
            return sb.toString();
        }


        private void asLines(int indentSize, List<String> lines) {
            String indent = new String(new char[indentSize]).replace('\0', ' ');
            for (MethodCall mc : methodCalls) {
                lines.add(indent + mc.toString());
                mc.asLines(indentSize + 2, lines);
            }
        }

        private List<String> asLines(int indentSize) {
            List<String> lines = new ArrayList<>();
            asLines(indentSize, lines);
            return lines;
        }

        public List<String> asLines() {
            return asLines(0);
        }

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @return the params
         */
        public List getParams() {
            return params;
        }

        /**
         * @return the prefix
         */
        public String getPrefix() {
            return prefix;
        }

        /**
         * @return the suffix
         */
        public String getSuffix() {
            return suffix;
        }

        /**
         * @return the parent
         */
        public MethodCall getParent() {
            return parent;
        }

        /**
         * @return the resultSetted
         */
        public boolean isResultSetted() {
            return resultSetted;
        }

        /**
         * @return the result
         */
        public Object getResult() {
            return result;
        }

        /**
         * @param result the result to set
         */
        public void setResult(Object result) {
            this.result = result;
            this.resultSetted = true;
        }
    }

    private static class CallMethodLog extends MethodCall {

        public CallMethodLog() {
            super(null);
        }
    }

    private static ThreadLocal<CallMethodLog> callMethodLog = new ThreadLocal<>();
    private static ThreadLocal<MethodCall> currentMethodCall = new ThreadLocal<>();
    private static ThreadLocal<Boolean> active = new ThreadLocal<>();

    private static CallMethodLog getLog() {
        if (callMethodLog.get() == null) {
            callMethodLog.set(new CallMethodLog());
        }
        return callMethodLog.get();
    }

    private static MethodCall getCurrentMethodCall() {
        if (currentMethodCall.get() == null) {
            currentMethodCall.set(getLog());
        }
        return currentMethodCall.get();
    }

    public static boolean isActive() {
        return active.get() != null && active.get();
    }

    public static void setActive(boolean activeValue) {
        active.set(activeValue);
    }

    public static void clear() {
        callMethodLog.set(new CallMethodLog());
        currentMethodCall.set(callMethodLog.get());
        active.set(false);
    }

    public static void start() {
        clear();
        active.set(true);
    }

    public static void stop() {
        active.set(false);
    }

    private static Object cloneObject(Object obj) {
        Class clazz = obj.getClass();
        try {
            Method cloneMethod = clazz.getDeclaredMethod("clone");
            Object res = cloneMethod.invoke(obj);
            return res;
        } catch (Exception e) {
            return null;
        }
    }

    private static List clone(List list) {
        if (list == null) {
            return null;
        }

        List result = (List) cloneObject(list);
        if (result == null) {
            result = list;
        }
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) instanceof List) {
                result.set(i, clone((List) list.get(i)));
            } else if (list.get(i) instanceof Cloneable) {
                Object obj = cloneObject(list.get(i));
                if (obj == null) {
                    obj = list.get(i);
                }
                result.set(i, obj);
            }
        }

        return result;
    }

    public static void enterWithAddInfo(String methodName, String prefix, String suffix, Object... methodParams) {
        if (isActive()) {
            List params = Arrays.asList(methodParams);
            MethodCall methodCall = new MethodCall(methodName, (List) clone(params), prefix, suffix, getCurrentMethodCall());
            getCurrentMethodCall().add(methodCall);
            currentMethodCall.set(methodCall);
        }
    }

    public static void enter(String methodName, Object... methodParams) {
        enterWithAddInfo(methodName, null, null, methodParams);
    }

    public static <T> T exit(T result) {
        if (isActive()) {
            getCurrentMethodCall().setResult(result);
            currentMethodCall.set(currentMethodCall.get().getParent());
        }
        return result;
    }

    public static void exit() {
        if (isActive()) {
            currentMethodCall.set(currentMethodCall.get().getParent());
        }
    }

    public static List<String> asLines() {
        return getLog().asLines();
    }

    public static String asTree() {
        return getLog().asTree();
    }
}
