circuit IO :
  module IO :
    input clock : Clock
    input reset : UInt<1>
    output io : { flip rst : UInt<1>, flip data_addr : UInt<32>, flip s3_inst : UInt<32>, cycle_p : UInt<32>, inst_p : UInt<32>, corr_B_p : UInt<32>, total_B_p : UInt<32>}

    reg prev_inst : UInt<32>, clock with :
      reset => (UInt<1>("h0"), prev_inst) @[IO.scala 25:22]
    reg cycle_p : UInt<32>, clock with :
      reset => (UInt<1>("h0"), cycle_p) @[IO.scala 26:20]
    reg inst_p : UInt<32>, clock with :
      reset => (UInt<1>("h0"), inst_p) @[IO.scala 27:19]
    reg corr_B_p : UInt<32>, clock with :
      reset => (UInt<1>("h0"), corr_B_p) @[IO.scala 28:21]
    reg total_B_p : UInt<32>, clock with :
      reset => (UInt<1>("h0"), total_B_p) @[IO.scala 29:22]
    io.cycle_p <= cycle_p @[IO.scala 31:14]
    io.inst_p <= inst_p @[IO.scala 32:13]
    io.corr_B_p <= corr_B_p @[IO.scala 33:15]
    io.total_B_p <= total_B_p @[IO.scala 34:16]
    node _T = eq(io.data_addr, UInt<32>("h80000018")) @[IO.scala 36:33]
    node _T_1 = or(io.rst, _T) @[IO.scala 36:16]
    when _T_1 : @[IO.scala 36:53]
      cycle_p <= UInt<1>("h0") @[IO.scala 37:13]
      inst_p <= UInt<1>("h0") @[IO.scala 38:12]
      corr_B_p <= UInt<1>("h0") @[IO.scala 39:14]
      total_B_p <= UInt<1>("h0") @[IO.scala 40:15]
      prev_inst <= UInt<5>("h13") @[IO.scala 41:15]
    else :
      node _cycle_p_T = add(cycle_p, UInt<1>("h1")) @[IO.scala 44:24]
      node _cycle_p_T_1 = tail(_cycle_p_T, 1) @[IO.scala 44:24]
      cycle_p <= _cycle_p_T_1 @[IO.scala 44:13]
      node _T_2 = neq(io.s3_inst, UInt<5>("h13")) @[IO.scala 45:22]
      node _T_3 = neq(io.s3_inst, prev_inst) @[IO.scala 45:47]
      node _T_4 = and(_T_2, _T_3) @[IO.scala 45:33]
      when _T_4 : @[IO.scala 45:62]
        prev_inst <= io.s3_inst @[IO.scala 46:17]
        node _inst_p_T = add(inst_p, UInt<1>("h1")) @[IO.scala 47:24]
        node _inst_p_T_1 = tail(_inst_p_T, 1) @[IO.scala 47:24]
        inst_p <= _inst_p_T_1 @[IO.scala 47:14]
      node _T_5 = bits(io.s3_inst, 6, 0) @[IO.scala 49:21]
      node _T_6 = eq(_T_5, UInt<7>("h63")) @[IO.scala 49:27]
      when _T_6 : @[IO.scala 49:50]
        node _total_B_p_T = add(total_B_p, UInt<1>("h1")) @[IO.scala 50:30]
        node _total_B_p_T_1 = tail(_total_B_p_T, 1) @[IO.scala 50:30]
        total_B_p <= _total_B_p_T_1 @[IO.scala 50:17]

