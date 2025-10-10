package im.bigs.pg.infra.persistence.partner.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

/**
 * DB용 Partner 엔티티.
 * - 도메인 모델과 1:1이 아니어도 되며, 저장 기술스택에 맞춘 컬럼/제약을 가질 수 있습니다.
 */
@Entity
@Table(name = "partner")
class PartnerEntity() {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    
    //아직 초기화되지 않은 필드를 안전하게 나중에 채우도록 허용하기 위함
    @Column(nullable = false, unique = true)
    lateinit var code: String

    @Column(nullable = false)
    lateinit var name: String

    @Column(nullable = false)
    var active: Boolean = true

    //의미없는 값드가면 안되니까 require로 체크
    constructor(code: String, name: String, active: Boolean = true) : this() {
        require(code.isNotBlank()) { "code값은 공백 X" }
        require(name.isNotBlank()) { "name값은 공백 X" }
        this.code = code
        this.name = name
        this.active = active
    }
}
