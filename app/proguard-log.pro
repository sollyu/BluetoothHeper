##########################################################################################################
# 作者：Sollyu
# 日期：2020-11-02
# 内容：发布版本移除日志，kotlin编译时带的而外信息，增强反调试难度
# 使用：proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro', 'proguard-log.pro'
##########################################################################################################
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** w(...);
    public static *** e(...);
}

##########################################################################################################
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    public static void checkExpressionValueIsNotNull(java.lang.Object, java.lang.String);
    public static void checkFieldIsNotNull(java.lang.Object, java.lang.String);
    public static void checkFieldIsNotNull(java.lang.Object, java.lang.String, java.lang.String);
    public static void checkNotNull(java.lang.Object);
    public static void checkNotNull(java.lang.Object, java.lang.String);
    public static void checkNotNullExpressionValue(java.lang.Object, java.lang.String);
    public static void checkNotNullParameter(java.lang.Object, java.lang.String);
    public static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
    public static void checkReturnedValueIsNotNull(java.lang.Object, java.lang.String);
    public static void throwUninitializedPropertyAccessException(java.lang.String);
}

-assumenosideeffects class java.util.Objects {
    public static java.lang.Object requireNonNull(java.lang.Object, java.lang.String);
}

##########################################################################################################
-assumenosideeffects interface org.slf4j.Logger {
    public void trace(...);
    public void debug(...);
    public void info(...);
    public void warn(...);
    public void error(...);

    public boolean isTraceEnabled(...);
    public boolean isDebugEnabled(...);
    public boolean isWarnEnabled(...);
}

-assumenosideeffects class org.slf4j.LoggerFactory {
    public static ** getLogger(...);
}