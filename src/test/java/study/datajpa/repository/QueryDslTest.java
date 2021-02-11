package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.domain.Member;
import study.datajpa.domain.Team;
import study.datajpa.repository.dto.MemberSearchCondition;
import study.datajpa.repository.dto.MemberTeamDto;

import javax.persistence.EntityManager;
import java.util.List;

@Transactional
@SpringBootTest
public class QueryDslTest {
    @Autowired private EntityManager em;
    @Autowired private MemberJpaRepository memberJpaRepository;

    @BeforeEach
    public void initDb() {
        Team teamA = Team.createTeam("teamA");
        Team teamB = Team.createTeam("teamB");
        Member memberA = Member.createMember("userA", 10, teamA);
        Member memberB = Member.createMember("userB", 20, teamA);
        Member memberC = Member.createMember("userC", 30, teamB);
        Member memberD = Member.createMember("userD", 40, teamB);

        em.persist(memberA);
        em.persist(memberB);
        em.persist(memberC);
        em.persist(memberD);
    }

    @Commit
    @Test
    public void test() throws Exception {
        MemberSearchCondition cond = new MemberSearchCondition(null, null, 11, 38);
        List<MemberTeamDto> search = memberJpaRepository.search(cond);
        Assertions.assertThat(search).extracting("username").contains("userB", "userC");

    }
}
