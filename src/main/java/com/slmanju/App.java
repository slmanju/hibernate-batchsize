package com.slmanju;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;
import java.util.function.Consumer;

public class App {

  private static EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("com.slmanju.batchsize");

  public static void main(String[] args) {
    populate();

    System.out.println("====================== Warm up completed =====================");

    read();

    entityManagerFactory.close();
  }

  public static void read() {
    transaction(entityManager -> {
      List<Foo> foos = entityManager.createQuery("from Foo", Foo.class).getResultList();
//      foos.get(0).getBars().isEmpty();
      for (Foo foo : foos) {
        foo.getBars().size();
      }
    });
  }

  public static void populate() {
    transaction(entityManager -> {
      for (int i = 0; i < 5; i++) {
        Foo foo = Foo.getInstance("Foo-" + i);
        for (int j = 0; j < 10; j++) {
          foo.addBar(Bar.getInstance("Bar-" + i + "-" + j));
        }
        entityManager.persist(foo);
      }
    });
  }

  public static void transaction(Consumer<EntityManager> consumer) {
    EntityManager entityManager = entityManagerFactory.createEntityManager();
    entityManager.getTransaction().begin();

    consumer.accept(entityManager);

    entityManager.getTransaction().commit();
    entityManager.close();
  }

}
