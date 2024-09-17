package org.example;

import java.util.Scanner;
import org.example.domen.Worker;
import org.example.domen.Departament;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import java.util.Scanner;
import java.sql.Date;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Configuration configuration = new Configuration().configure();
        try (SessionFactory sessionFactory = configuration.buildSessionFactory()) {
            Scanner scanner = new Scanner(System.in);
            boolean continueLoop = true;
            while (continueLoop) {

                System.out.println("Выберите операцию(обязательно! первым надо создавать департамент. Директор добавляется позже)");
                //scanner.nextLine();
                System.out.println("1. Создать департамент");
                System.out.println("2. Добавить сотрудника");
                System.out.println("3. Найти сотрудника по ID");
                System.out.println("4. Найти сотрудника по имени");
                System.out.println("5. Найти сотрудника по дате рождения");
                System.out.println("6. Вычислить сумму зарплат");
                System.out.println("7. Обновления в департаменте");
                System.out.println("8. Обновления у сотрудника");
                System.out.println("9. Найти сотрудника по наз-ю департамента");
                System.out.println("10. Обновить директора департамента");
                System.out.println("11. Вывести департаменты");
                System.out.println("12. Вывести сотрудников");
                System.out.println("13. Удалить сотрудника");
                System.out.println("14. Завершение работы");
                System.out.println("ВВедите номер операции: ");
                int choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        createDep(sessionFactory);
                        break;
                    case 2:
                        addWorker(sessionFactory);
                        break;
                    case 3:
                        findWorkerbyId(sessionFactory);
                        break;
                    case 4:
                        findWorkerbyName(sessionFactory);
                        break;
                    case 5:
                        findWorkerByBirthDate(sessionFactory);
                        break;
                    case 6:
                        calculateTotalSalary(sessionFactory);
                        break;
                    case 7:
                        updateDepartment(sessionFactory);
                        break;
                    case 8:
                        updateWorker(sessionFactory);
                        break;
                    case 9:
                        findworkbydep(sessionFactory);
                        break;
                    case 10:
                        updateDepartmentDirector(sessionFactory);
                        break;
                    case 11:
                        printDepartaments(sessionFactory);
                        break;
                    case 12:
                        printWorkers(sessionFactory);
                        break;
                    case 13:
                            deleteWorker(sessionFactory);
                        break;
                    case 14:
                        continueLoop = false;
                        break;
                    default:
                        System.out.println("Неверный ввод, попробуйте снова!");
                }
            }
        }
    }


    public static void createDep(SessionFactory sessionFactory) {
        Scanner scanner = new Scanner(System.in);
        Departament departament = new Departament();
        System.out.print("ВВедите ID Департамента: ");
        departament.setId(scanner.nextInt());
        System.out.print("Введите название Департамента: ");
        departament.setName(scanner.next());
        System.out.print("Введите описание Департамента: ");
        departament.setDescription(scanner.next());
        //departament.setDirector(null);

        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.persist(departament);
        session.getTransaction().commit();
        System.out.println("Департамент создан: " + departament.getName());
    }

    public static void addWorker(SessionFactory sessionFactory) {
        Scanner scanner = new Scanner(System.in);

        Worker worker = new Worker();
        System.out.print("Введите ID работника: ");
        worker.setId(scanner.nextInt());
        scanner.nextLine();

        System.out.print("Введите имя работника: ");
        worker.setName(scanner.nextLine());

        System.out.print("Введите фамилию работника: ");
        worker.setSecondName(scanner.nextLine());

        System.out.print("Введите дату рождения работника (yyyy-MM-dd): ");
        worker.setDateOfBirth(Date.valueOf(scanner.nextLine()));

        System.out.print("Введите место рождения работника: ");
        worker.setPlaceOfBirth(scanner.nextLine());

        System.out.print("Введите ЗП работника: ");
        worker.setSalary(scanner.nextDouble());
        scanner.nextLine();

        System.out.print("Введите ID департамента работника: ");
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
    private static void findWorkerbyId(SessionFactory sessionFactory) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите ID работника: ");
        int workerId = scanner.nextInt();

        try (Session session = sessionFactory.openSession()) {
            Worker worker = session.find(Worker.class, workerId);
            if (worker != null) {
                System.out.println("Работник найден по ID: " + worker);
            } else {
                System.out.println("Работник с ID " + workerId + " не найден");
            }
        }
    }

    private static void findWorkerbyName(SessionFactory sessionFactory) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите имя работника: ");
        String workerName = scanner.next();

        try (Session session = sessionFactory.openSession()) {
            Query<Worker> query = session.createQuery("from workers w where w.name = :workerName", Worker.class);
            query.setParameter("workerName", workerName);
            List<Worker> workers = query.getResultList();
            if (!workers.isEmpty()) {
                for (Worker worker : workers) {
                    System.out.println("Работник найден по имени: " + worker);
                }
            } else {
                System.out.println("Работник с именем " + workerName + " не найден");
            }
        }
    }

    private static void findWorkerByBirthDate(SessionFactory sessionFactory) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите год рождения работника (в формате yyyy-MM-dd): ");
        String birthDateInp = scanner.next();
        Date birthDate = Date.valueOf(birthDateInp);
        try (Session session = sessionFactory.openSession()) {
            Query<Worker> query = session.createQuery("from workers w where w.dateOfBirth = :birthDate", Worker.class);
            query.setParameter("birthDate", birthDate);
            List<Worker> workers = query.getResultList();
            for (Worker worker : workers) {
                System.out.println("Работник найден по году рождения: " + worker);
            }
        }
    }

    public static void deleteWorker(SessionFactory sessionFactory) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите ID работника для удаления: ");
        int workerId = scanner.nextInt();
        scanner.nextLine();

        // Retrieve the Worker object from the database
        Worker worker = session.get(Worker.class, workerId);

        // Update department's director ID to null if the worker is a director
        Query query = session.createQuery("UPDATE departments SET director = null WHERE director = :worker");
        query.setParameter("worker", worker);
        query.executeUpdate();

        Departament department = worker.getDepartament();
        if (department != null) {
            department.setDirector(null);
        }
        session.remove(worker);

        session.getTransaction().commit();
    }

    private static void calculateTotalSalary(SessionFactory sessionFactory) {
        try (Session session = sessionFactory.openSession()) {
            Query query = session.createQuery("SELECT SUM(w.salary) FROM workers w");
            Double totalSalary = (Double) query.uniqueResult();
            System.out.println("Сумма зп: " + totalSalary);
        }
    }

    private static void updateDepartment(SessionFactory sessionFactory) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Введите ID отдела для обновления: ");
        int departmentId = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Выберите, что хотите обновить:");
        System.out.println("1. Название");
        System.out.println("2. Описание");
        System.out.print("Введите номер выбора: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        try (Session session = sessionFactory.openSession()) {
            Departament department = session.get(Departament.class, departmentId);
            if (department != null) {
                switch (choice) {
                    case 1:
                        System.out.print("Введите новое название: ");
                        String newDepartmentName = scanner.nextLine();
                        department.setName(newDepartmentName);
                        break;
                    case 2:
                        System.out.print("Введите новое описание: ");
                        String newDepartmentDescription = scanner.nextLine();
                        department.setDescription(newDepartmentDescription);
                        break;
                    default:
                        System.out.println("Неправильный выбор");
                        return;
                }

                session.beginTransaction();
                session.merge(department);
                session.getTransaction().commit();
                System.out.println("Отдел успешно обновлен");
            } else {
                System.out.println("Отдел не найден");
            }
        }
    }

    public static void updateDepartmentDirector(SessionFactory sessionFactory) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Введите ID департамента: ");
        int departmentId = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Введите ID нового директора: ");
        int newDirectorId = scanner.nextInt();
        scanner.nextLine();

        try (Session session = sessionFactory.openSession()) {
            Departament department = session.get(Departament.class, departmentId);
            if (department != null) {
                Worker newDirector = session.get(Worker.class, newDirectorId);
                if (newDirector != null) {
                    department.setDirector(newDirector);
                    session.beginTransaction();
                    session.merge(department);
                    session.getTransaction().commit();
                    System.out.println("Директор департамента обновлен");
                } else {
                    System.out.println("Директор с идентификатором " + newDirectorId + " не найден");
                }
            } else {
                System.out.println("Департамент с идентификатором " + departmentId + " не найден");
            }
        }
    }

    /*private static void updateDepartmentDirector(SessionFactory sessionFactory, int departmentId, int newDirectorId) {
        try (Session session = sessionFactory.openSession()) {
            Departament department = session.get(Departament.class, departmentId);
            if (department != null) {
                Worker newDirector = session.get(Worker.class, newDirectorId);
                if (newDirector != null) {
                    department.setDirector(newDirector);
                    session.beginTransaction();
                    session.merge(department);
                    session.getTransaction().commit();
                } else {
                    System.out.println("Директор с идентификатором " + newDirectorId + " не найден");
                }
            } else {
                System.out.println("Департамент с идентификатором " + departmentId + " не найден");
            }
        }
    }*/

    public static void updateWorker(SessionFactory sessionFactory) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Введите ID работника: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Выберите, что хотите обновить:");
        System.out.println("1. Имя");
        System.out.println("2. Фамилия");
        System.out.println("3. Дата рождения");
        System.out.println("4. Место рождения");
        System.out.println("5. Зарплата");
        System.out.println("6. Департамент");
        System.out.print("Введите номер выбора: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        try (Session session = sessionFactory.openSession()) {
            Worker w = session.get(Worker.class, id);
            if (w != null) {
                switch (choice) {
                    case 1:
                        System.out.print("Введите новое имя: ");
                        String name = scanner.nextLine();
                        w.setName(name);
                        break;
                    case 2:
                        System.out.print("Введите новую фамилию: ");
                        String surname = scanner.nextLine();
                        w.setSecondName(surname);
                        break;
                    case 3:
                        System.out.print("Введите новую дату рождения (в формате dd/MM/yyyy): ");
                        String birthDate = scanner.nextLine();
                        w.setDateOfBirth(Date.valueOf(birthDate));
                        break;
                    case 4:
                        System.out.print("Введите новое место рождения: ");
                        String birthPlace = scanner.nextLine();
                        w.setPlaceOfBirth(birthPlace);
                        break;
                    case 5:
                        System.out.print("Введите новую зарплату: ");
                        double salary = scanner.nextDouble();
                        scanner.nextLine();
                        w.setSalary(salary);
                        break;
                    case 6:
                        System.out.print("Введите ID департамента: ");
                        int departmentId = scanner.nextInt();
                        scanner.nextLine();
                        Departament department = session.get(Departament.class, departmentId);
                        if (department != null) {
                            w.setDepartament(department);
                        } else {
                            System.out.println("Департамент с идентификатором " + departmentId + " не найден");
                            return;
                        }
                        break;
                    default:
                        System.out.println("Неправильный выбор");
                        return;
                }

                session.beginTransaction();
                session.merge(w);
                session.getTransaction().commit();
                System.out.println("Информация о работнике обновлена");
            } else {
                System.out.println("Работник с идентификатором " + id + " не найден");
            }
        }
    }

    /*private static void findWorkersByDepartment(SessionFactory sessionFactory, String departmentName) {
        try (Session session = sessionFactory.openSession()) {
            Query<Worker> query = session.createQuery("FROM workers w WHERE w.department = :departmentName", Worker.class);
            query.setParameter("departmentName", departmentName);
            List<Worker> workers = query.getResultList();
            for (Worker worker : workers) {
                System.out.println("Работник: " + worker.getName() + " - Департамент: " + worker.getDepartament());
            }
        }
    }*/

    /*private static void findWorkerByDepartmentId(SessionFactory sessionFactory, int departmentId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Worker> query = session.createQuery("from workers w where w.department = :departmentId", Worker.class);
            query.setParameter("departmentId", departmentId);
            List<Worker> workers = query.getResultList();
            for (Worker worker : workers) {
                System.out.println("Работник: " + worker.getName() + " - Департамент: " + worker.getDepartament().getName());
            }
        }
    }*/

    /*private static Departament getDepartment(SessionFactory sessionFactory, String departmentName) {
        try (Session session = sessionFactory.openSession()) {
            Query<Departament > query = session.createQuery("FROM Department d WHERE d.name = :departmentName", Departament.class);
            query.setParameter("departmentName", departmentName);
            return query.uniqueResult();
        }
    }*/

    public static void findworkbydep(SessionFactory sessionFactory) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Введите название департамента: ");
        String departmentName = scanner.nextLine();

        try (Session session = sessionFactory.openSession()) {
            Query<Departament> query = session.createQuery("from departments d where d.name = :departmentName", Departament.class);
            query.setParameter("departmentName", departmentName);
            Departament departament = query.uniqueResult();
            if (departament != null) {
                System.out.println("Department: " + departament.getName());
                Query<Worker> workerQuery = session.createQuery("from workers w where w.department = :department", Worker.class);
                workerQuery.setParameter("department", departament);
                List<Worker> workers = workerQuery.getResultList();
                for (Worker worker : workers) {
                    System.out.println(" Работник: " + worker.getName() + " " + worker.getSecondName());
                }
            } else {
                System.out.println("Департамент не был найден: " + departmentName);
            }
        }
    }

    private static void printDepartaments(SessionFactory sessionFactory) {
        try (Session session = sessionFactory.openSession()) {
            Query<Departament> query = session.createQuery("from departments", Departament.class);
            List<Departament> departaments = query.getResultList();
            for (Departament departament : departaments) {
                System.out.println(departament);
            }
        }
    }

    private static void printWorkers(SessionFactory sessionFactory) {
        try (Session session = sessionFactory.openSession()) {
            Query<Worker> query = session.createQuery("from workers", Worker.class);
            List<Worker> workers = query.getResultList();
            for (Worker worker : workers) {
                System.out.println(worker);
            }
        }
    }
}



    /*private static void addWorkers(SessionFactory sessionFactory){
        Worker w = new Worker("John", "Doe", Date.valueOf("1990-01-01"),5000.0);
        Worker w1 = new Worker("Jane", "Doe", Date.valueOf("1992-01-01"), 6000.0);
        Worker w2 = new Worker("Bob", "Smith", Date.valueOf("1995-01-01"), 7000.0);
        try{ Session session = sessionFactory.openSession();
            Transaction tr = session.beginTransaction();
            session.persist(w);
            session.persist(w1);
            session.persist(w2);
            tr.commit();} catch (HibernateException e) {
            throw new RuntimeException(e);
        }
    }*/


