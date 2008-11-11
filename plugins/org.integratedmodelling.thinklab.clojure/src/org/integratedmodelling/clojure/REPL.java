package org.integratedmodelling.clojure;

/**
 *   Copyright (c) Rich Hickey. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Common Public License 1.0 (http://opensource.org/licenses/cpl.php)
 *   which can be found in the file CPL.TXT at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 **/

/* rich Oct 18, 2007 */

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import clojure.lang.LineNumberingPushbackReader;
import clojure.lang.LispReader;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Var;
import clojure.lang.Compiler;

public class REPL {

	
	private InputStream input = System.in;
	private OutputStream output = System.out;

	public void run(String[] args) throws Exception {

		final Symbol USER = Symbol.create("user");
		final Symbol CLOJURE = Symbol.create("clojure.core");
		
		final Var in_ns = RT.var("clojure.core", "in-ns");
		final Var refer = RT.var("clojure.core", "refer");
		final Var ns = RT.var("clojure.core", "*ns*");
		final Var compile_path = RT.var("clojure.core", "*compile-path*");
		final Var warn_on_reflection = RT.var("clojure.core",
				"*warn-on-reflection*");
		final Var print_meta = RT.var("clojure.core", "*print-meta*");
		final Var print_length = RT.var("clojure.core", "*print-length*");
		final Var print_level = RT.var("clojure.core", "*print-level*");
		final Var star1 = RT.var("clojure.core", "*1");
		final Var star2 = RT.var("clojure.core", "*2");
		final Var star3 = RT.var("clojure.core", "*3");
		final Var stare = RT.var("clojure.core", "*e");

		// RT.init();

		try {
			// *ns* must be thread-bound for in-ns to work
			// thread-bind *warn-on-reflection* so it can be set!
			// thread-bind *1,*2,*3,*e so each repl has its own history
			// must have corresponding popThreadBindings in finally clause
			Var.pushThreadBindings(RT.map(ns, ns.get(), warn_on_reflection,
					warn_on_reflection.get(), print_meta, print_meta.get(),
					print_length, print_length.get(), print_level, print_level
							.get(), compile_path, "classes", star1, null,
					star2, null, star3, null, stare, null));

			// create and move into the user namespace
			in_ns.invoke(USER);
			refer.invoke(CLOJURE);

			// load any supplied files
			for (String file : RT.processCommandLine(args))
				Compiler.loadFile(file);

			// repl IO support
			LineNumberingPushbackReader rdr = new LineNumberingPushbackReader(
					new InputStreamReader(input, RT.UTF8));
			OutputStreamWriter w = new OutputStreamWriter(output);
			
			Object EOF = new Object();

			// start the loop
			w.write("Clojure\n");
			for (;;) {
				try {
					w.write(/* Compiler.currentNS().name + */ "=> ");
					w.flush();
					Object r = LispReader.read(rdr, false, EOF, false);
					if (r == EOF) {
						w.write("\n");
						w.flush();
						break;
					}
					Object ret = Compiler.eval(r);
					RT.print(ret, w);
					w.write('\n');
					// w.flush();
					star3.set(star2.get());
					star2.set(star1.get());
					star1.set(ret);
				} catch (Throwable e) {
					Throwable c = e;
					while (c.getCause() != null)
						c = c.getCause();
					RT.print(e instanceof Compiler.CompilerException ? e
									: c, w);
					stare.set(e);
				}
			}
		} catch (Exception e) {
			e.printStackTrace(/*(PrintWriter) RT.ERR.get()*/);
		} finally {
			Var.popThreadBindings();
		}
		System.exit(0);
	}

	public void setInput(InputStream inputStream) {
		this.input = inputStream;
	}

	public void setOutput(OutputStream outputStream) {
		this.output = outputStream;
	}

}
