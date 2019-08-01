replace() {
  python -c "if True:
    wsc = ['CharBuffer', 'Chars', 'Character', 'Chr', 'char']
    wsd = ['DoubleBuffer', 'Doubles', 'Double', 'Dbl', 'double']
    wsf = ['FloatBuffer', 'Floats', 'Float', 'Flt', 'float']
    wsi = ['IntBuffer', 'Ints', 'Integer', 'Int', 'int']
    wsl = ['LongBuffer', 'Longs', 'Long', 'Lng', 'long']

    repls0 = [wsc, wsd, wsf, wsi, wsl]
    repls1 = [wsc, wsd, wsf, wsi, wsl]
    repls2 = [wsc, wsd, wsf, wsi, wsl]

    def r0(ws0, v, s):
      for i in range(len(ws0)): s = s.replace(ws0[i], '{' + v + str(i) + '}')
      return s

    def r1(v, ws1, s):
      for i in range(len(ws1)): s = s.replace('{' + v + str(i) + '}', ws1[i])
      return s

    def replace(ws0, ws1, ws2, s):
      s = r0(wsc, 'x', (r0(wsd, 'y', (r0(wsf, 'z', (s))))))
      s = r1('x', ws0, (r1('y', ws1, (r1('z', ws2, (s))))))
      return s

    for repl0 in repls0:
      for repl1 in repls1:
        for repl2 in repls2:
          filename0 = '${1}'
          filename1 = replace(repl0, repl1, repl2, filename0)

          s0 = None
          with open(filename0) as f0: s0 = f0.read()
          s1 = replace(repl0, repl1, repl2, s0)
          sx, line0 = '', ''
          for line in s1.split('\n'):
            if line0 != line:
              sx, line0 = sx + line + '\n', line
          with open(filename1, 'w') as f1: f1.write(sx[:-1])
  "
}

replace src/main/java/primal/primitive/adt/pair/ChrDblPair.java
replace src/main/java/primal/primitive/adt/pair/ChrObjPair.java
replace src/main/java/primal/primitive/fp/ChrFunUtil.java
replace src/main/java/primal/primitive/fp/ChrObjFunUtil.java
replace src/main/java/primal/primitive/Chr_Dbl.java
replace src/main/java/primal/primitive/ChrDblPredicate.java
replace src/main/java/primal/primitive/ChrDblSink.java
replace src/main/java/primal/primitive/ChrDblSource.java
replace src/main/java/primal/primitive/ChrDbl_Flt.java
replace src/main/java/primal/primitive/ChrDbl_Obj.java
#replace src/main/java/primal/primitive/ChrDblFunUtil.java
replace src/main/java/primal/primitive/ChrObj_Dbl.java
replace src/main/java/primal/primitive/ChrOpt.java
replace src/main/java/primal/primitive/ChrPrim.java
