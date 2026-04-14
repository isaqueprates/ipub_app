# Kotlin metadata
-keep class kotlin.Metadata { *; }

# Mantém classes top-level do Kotlin (arquivos Kt)
-keepclassmembers class **Kt {
    *;
}

# Mantém atributos necessários para reflexão
-keepattributes InnerClasses, EnclosingMethod, Signature, RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Firestore / Firebase
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**
-keep class com.google.protobuf.** { *; }
-dontwarn com.google.protobuf.**
-keep class com.google.firebase.auth.** { *; }
-dontwarn com.google.firebase.auth.**
-keep class com.google.firebase.analytics.** { *; }
-dontwarn com.google.firebase.analytics.**

# Mantém suas classes de modelo (ajuste o pacote se necessário)
-keep class com.ipub.ipub_app.data.** { *; }

# Jetpack Compose
-keep class androidx.compose.** { *; }
-keepclassmembers class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Material icons extended
-keep class androidx.compose.material.icons.** { *; }

# Lifecycle / ViewModel / LiveData / Flow
-keep class androidx.lifecycle.** { *; }
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# Kotlin coroutines
-dontwarn kotlinx.coroutines.**
-keep class kotlinx.coroutines.** { *; }

# Classes usadas por reflection (GSON, Moshi, etc.)
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Não remover classes usadas por reflection em runtime
-keepattributes *Annotation*

# Evita remoção de métodos principais que o sistema espera
-keepclassmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

# Preserve enumerations (se precisar)
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Não obfuscar nomes de recursos (opcional, mas útil)
-keepclassmembers class **.R$* {
    public static <fields>;
}
# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile