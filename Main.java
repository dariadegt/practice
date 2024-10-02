package org.example;

import org.example.domen.Course;
import org.example.domen.Student;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;


public class Main {

    public static void main(String[] args) {
        SessionFactory factory = new Configuration().configure().buildSessionFactory();

        // Создание курсов
        Course course1 = new Course("Mathematics");
        Course course2 = new Course("Physics");
        Course course3 = new Course("Computer Science");
        Course course4 = new Course("Biology");

        // Открытие сессии и начало транзакции
        Session session = factory.openSession();
        session.beginTransaction();

        // Создание студентов
        createStudents(session, 20);

        // Добавление студентов на курсы
        addStudentsToCourses(session, 20, course1, course2, course3, course4);

        // Добавление курсов в базу данных
        persistCourses(session, course1, course2, course3, course4);

        // Завершение транзакции и закрытие сессии
        session.getTransaction().commit();
        session.close();

        // Открытие новой сессии и начало транзакции
        session = factory.openSession();
        session.beginTransaction();

        // Вывод студентов
        printStudents(session);

        // Вывод курсов
        printCourses(session);

        // Удаление студентов с курса "Mathematics"
        removeStudentsFromCourse(session, "Mathematics");

        // Удаление конкретного студента
        removeStudentByName(session, "Student 10");

        removeCoursebyName(session,"Biology");

        // Удаление курсов
        removeAllCourses(session);

        // Завершение транзакции и закрытие сессии
        session.getTransaction().commit();
        session.close();

        // Закрытие фабрики сессий
        factory.close();
    }

    public static void createStudents(Session session, int numberOfStudents) {
        for (int i = 0; i < numberOfStudents; i++) {
            Student student = new Student("Student " + (i + 1));
            session.persist(student);
        }
    }

    public static void addStudentsToCourses(Session session, int numberOfStudents, Course... courses) {
        for (int i = 0; i < numberOfStudents; i++) {
            Student student = session.get(Student.class, i + 1);
            if (i < 5) {
                student.getCourses().add(courses[0]);
            } else if (i < 10) {
                student.getCourses().add(courses[1]);
            } else if (i < 15) {
                student.getCourses().add(courses[2]);
            } else {
                student.getCourses().add(courses[3]);
            }
            session.persist(student);
        }
    }

    public static void persistCourses(Session session, Course... courses) {
        for (Course course : courses) {
            session.persist(course);
        }
    }

    public static void printStudents(Session session) {
        System.out.println("Студенты:");
        for (Student student : session.createQuery("FROM student", Student.class).getResultList()) {
            System.out.println(student);
        }
    }

    public static void printCourses(Session session) {
        System.out.println("\nКурсы:");
        for (Course course : session.createQuery("FROM course", Course.class).getResultList()) {
            System.out.println(course);
        }
    }

    public static void removeStudentsFromCourse(Session session, String courseName) {
        Query<Student> query = session.createQuery(
                "SELECT s FROM student s JOIN s.courses c WHERE c.name = :courseName", Student.class);
        query.setParameter("courseName", courseName);

        for (Student student : query.getResultList()) {
            session.remove(student);
            System.out.println("Удален студент с курса " + courseName);
        }
    }

    public static void removeStudentByName(Session session, String studentName) {
        Query<Student> query = session.createQuery(
                "FROM student s WHERE s.name = :studentName", Student.class);
        query.setParameter("studentName", studentName);
        Student student = query.uniqueResult();
        if (student != null) {
            session.remove(student);
            System.out.println("Удален студент с именем " + studentName);
        } else {

            System .out.println("Студент с именем " + studentName + " не найден");

        }

    }


    public static void removeAllCourses(Session session) {
        for (Course course : session.createQuery("FROM course", Course.class).getResultList()) {
            session.remove(course);
        }
    }

    public static void removeCoursebyName(Session session, String name) {
        Query<Course> query = session.createQuery(
                "FROM course s WHERE s.name = :name", Course.class);
        query.setParameter("name", name);
        Course student = query.uniqueResult();
        if (student != null) {
            session.remove(student);
            System.out.println("Удален курс с названием " + name);
        } else {

            System .out.println("Курс с названием " + name + " не найден");

        }
    }
}