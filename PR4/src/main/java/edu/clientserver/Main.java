package edu.clientserver;

import edu.clientserver.domain.Good;
import edu.clientserver.domain.Group;
import edu.clientserver.inittilzer.DatabaseInitializer;
import edu.clientserver.repository.GoodRepository;
import edu.clientserver.repository.GroupRepository;

import java.math.BigDecimal;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try {
            DatabaseInitializer.createTables();
            GoodRepository repository = new GoodRepository();
            GroupRepository groupRepository = new GroupRepository();

            Group group1 = groupRepository.create(Group.builder().name("group 1").build());
            Group group2 = groupRepository.create(Group.builder().name("group 2").build());

            Good good1 = repository.create(Good.builder().name("good 1").price(BigDecimal.valueOf(10.00)).quantity(10).group(group1).build());
            Good good2 = repository.create(Good.builder().name("good 2").price(BigDecimal.valueOf(11.00)).quantity(11).group(group1).build());
            Good good3 = repository.create(Good.builder().name("good 3").price(BigDecimal.valueOf(100)).quantity(1).group(group1).build());

            System.out.println(repository.read(good1.getId()));
            System.out.println(repository.listByCriteria("quantity >= 10"));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}