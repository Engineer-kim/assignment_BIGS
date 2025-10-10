package im.bigs.pg.application.pg.port.out

import im.bigs.pg.application.payment.port.out.TestPgApprovalCommand

interface TestPgClientOutPort {
    fun supports(partnerId: Long): Boolean
    fun approve(command: TestPgApprovalCommand): PgApproveResult
}
