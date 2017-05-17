data class Result (val path: String, val score:String, val code:String, val err:String, val output:String)
/*
If you want to add more attributes to the Result class, it's real easy. All you have to do is add another argument to
this constructor here, and it will automatically make getter and setter method's for it. The syntax is easy too!
It's just "val (name): (Type)" seperated by commas. "val" is short for value, name is whatever you want that attribute's
name to be, and the Type is any standard Java type. The only little oddity is that primitives use capital names in Kotlin.
some examples:
val x:Int, val y:Double, val z:String, val asdf:FileBrowser
You can use any java or Kotlin class, and they are interchangable. You can even turn a java class into a kotlin one
automatically if you want! For example, the kotlin version of the

 */

