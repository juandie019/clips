package test;

import net.sf.clipsrules.jni.*;

public class BasicDemo {
    public static void main(String args[]) throws Exception {
        Environment clips;
        
        clips = new Environment();
        clips.eval("(clear)");
        clips.load("/Users/diego/Documents/CLIPSJNI/work/clps/test.clp");
        clips.eval("(reset)");
        clips.eval("(facts)");
        clips.run();
    }

}