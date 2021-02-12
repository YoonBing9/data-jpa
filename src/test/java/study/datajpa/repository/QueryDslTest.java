package study.datajpa.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.domain.Member;
import study.datajpa.domain.Team;
import study.datajpa.repository.dto.MemberSearchCondition;
import study.datajpa.repository.dto.MemberTeamDto;
import study.datajpa.repository.query.MemberQueryRepository;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
public class QueryDslTest {
    @Autowired private EntityManager em;
    @Autowired private MemberJpaRepository memberJpaRepository;
    @Autowired private MemberQueryRepository memberQueryRepository;

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

    @Test
    public void test() throws Exception {
        MemberSearchCondition cond = new MemberSearchCondition(null, null, 11, 38);
        List<MemberTeamDto> search = memberJpaRepository.search(cond);
        assertThat(search).extracting("username").contains("userB", "userC");

    }

    @Test
    public void searchWithPageComplex() throws Exception {
        MemberSearchCondition cond = new MemberSearchCondition(null, null, null, null);
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.ASC, "username"));

        Page<MemberTeamDto> results = memberQueryRepository.searchWithPageComplex(cond, pageRequest);

        assertThat(results).extracting("username").contains("userA","userB","userC");
    }
}
