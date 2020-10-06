/*
 * The MIT License
 *
 * Copyright 2020 Declan Thompson.
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

/**
 * The JustDouble class implements NDJust for justifications requiring 2 args
 * 
 * @since Proof Assistant 2.0
 * @version 1.0
 * @author Declan Thompson
 */
public class JustDouble extends NDJustification implements NDJust {
    // Type Constants
    public static final int CON_INTRO = 3;
    
    
    private int type;
    private NDLine firstLine;
    private NDLine secondLine;
    
    public JustDouble(int tp, NDLine one, NDLine two) {
        this.type = tp;
        this.firstLine = one;
        this.secondLine = two;
        setBlank(false);
    }
}
