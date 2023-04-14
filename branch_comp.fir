circuit branch_comp :
  module branch_comp :
    input clock : Clock
    input reset : UInt<1>
    output io : { flip rs1 : UInt<32>, flip rs2 : UInt<32>, flip BrUn : UInt<1>, BrEq : UInt<1>, BrLt : UInt<1>}

    node _T = eq(io.BrUn, UInt<1>("h1")) @[branch_comp.scala 22:17]
    when _T : @[branch_comp.scala 22:29]
      node _io_BrLt_T = lt(io.rs1, io.rs2) @[branch_comp.scala 23:23]
      io.BrLt <= _io_BrLt_T @[branch_comp.scala 23:13]
      node _io_BrEq_T = eq(io.rs1, io.rs2) @[branch_comp.scala 24:23]
      io.BrEq <= _io_BrEq_T @[branch_comp.scala 24:13]
    else :
      node _io_BrLt_T_1 = asSInt(io.rs1) @[branch_comp.scala 27:23]
      node _io_BrLt_T_2 = asSInt(io.rs2) @[branch_comp.scala 27:39]
      node _io_BrLt_T_3 = lt(_io_BrLt_T_1, _io_BrLt_T_2) @[branch_comp.scala 27:30]
      io.BrLt <= _io_BrLt_T_3 @[branch_comp.scala 27:13]
      node _io_BrEq_T_1 = asSInt(io.rs1) @[branch_comp.scala 28:23]
      node _io_BrEq_T_2 = asSInt(io.rs2) @[branch_comp.scala 28:41]
      node _io_BrEq_T_3 = eq(_io_BrEq_T_1, _io_BrEq_T_2) @[branch_comp.scala 28:30]
      io.BrEq <= _io_BrEq_T_3 @[branch_comp.scala 28:13]

