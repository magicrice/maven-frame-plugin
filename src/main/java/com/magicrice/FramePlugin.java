package com.magicrice;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

@Mojo(name = "afterCompile", defaultPhase = LifecyclePhase.PROCESS_CLASSES)
public class FramePlugin extends AbstractMojo {
    @Parameter(name = "output", defaultValue = "${project.build.directory}")
    private File output;

    public void execute() throws MojoExecutionException {
//        File file = new File("/home/zhangkaiyue/workspace/report_server/target/classes/com/taijihuabao");
        ModifyOriginalClass.insert(output);
    }
}
