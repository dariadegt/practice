package org.example;

import org.example.domen.Connect;
import org.example.domen.Course;
import org.example.domen.Student;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.sql.Date;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Configuration configuration = new Configuration().configure();
        try (SessionFactory sessionFactory = configuration.buildSessionFactory()) {

            printStudents(sessionFactory);


        }


    }

    public static void addStudent(SessionFactory sessionFactory) {
        Scanner scanner = new Scanner(System.in);

        Student student = new Student();
        System.out.print("Введите ID работника: ");
        student.setId(1);

        student.setName(scanner.nextLine());

        int departmentId = scanner.nextInt();
        scanner.nextLine();

        Session session = sessionFactory.openSession();
        Departament department = session.find(Departament.class, departmentId);
        if (department == null) {
            System.out.println("Департамент с ID " + departmentId + " не существует.");
            return;
        }
        worker.setDepartament(department);
        session.beginTransaction();
        session.persist(worker);
        session.getTransaction().commit();
        System.out.println("Работник добавлен: " + worker.getName() + " " + worker.getSecondName());

    }

    private static void printStudents(SessionFactory sessionFactory) {
        try (Session session = sessionFactory.openSession()) {
            Query<Student> query = session.createQuery("from student", Student.class);
            List<Student> students = query.getResultList();
            for (Student student : students) {
                System.out.println(student);
            }
        }
    }
}