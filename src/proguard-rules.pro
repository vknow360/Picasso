# Add any ProGuard configurations specific to this
# extension here.

-keep public class com.sunny.Picasso.Picasso {
    public *;
 }
-keeppackagenames gnu.kawa**, gnu.expr**

-optimizationpasses 4
-allowaccessmodification
-mergeinterfacesaggressively
-dontusemixedcaseclassnames
-repackageclasses 'com/sunny/Picasso/repack'
-flattenpackagehierarchy
-dontpreverify
-dontwarn
