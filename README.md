# maven-frame-plugin
asm maven plugin
执行mvn install

导入插件
            <plugin>
                <groupId>com.magicrice</groupId>
                <artifactId>maven-frame-plugin</artifactId>
                <version>1.0-SNAPSHOT</version>
                <executions>
                    <execution>
                        <goals>
                            <!-- 执行目标 -->
                            <goal>afterCompile</goal>
                        </goals>
                        <!-- 执行这个目标所在的生命周期 -->
                        <phase>compile</phase>
                    </execution>
                </executions>
            </plugin>
            
            
            
