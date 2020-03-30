/*
 * The MIT License
 *
 * Copyright 2014 Declan.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package proofassistant;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 *
 * @author Declan
 */
public class ProofSystem<String> extends ArrayList<String> {
    private String nameOfSystem;
    private String axioms = (String) "";
    public ProofSystem(String name) {
        super();
        this.nameOfSystem = name;
    }
    
    public ProofSystem(String name, ProofSystem other) { // New proof system based on another
        super();
        this.nameOfSystem = name;
        addAll(other);
    }
    
    public ProofSystem(String name, HashSet<String> other) {
        super();
        this.nameOfSystem = name;
        addAll(other);
    }
    
    public String getName() {
        return nameOfSystem;
    }
    
    public void setAxioms(String newAxioms) {
        axioms = newAxioms;
    }
    
    public String getAxioms() {
        return axioms;
    }
    
    public boolean supportsRuleSet(HashSet<String> ruleSet) {
        Iterator<String> iterate = ruleSet.iterator();
//        System.out.println("testing system " + nameOfSystem);
        while (iterate.hasNext()) {
            String rule = iterate.next();
            if (!contains(rule)) {
//                System.out.println("doesn't contain " + rule);
                return false;
            }
        }
        return true;
    }
}
