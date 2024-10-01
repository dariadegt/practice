package org.example.domen;


import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity(name = "student")
@Table(name = "student")
public class Student {
    private Set<Course> courses = new HashSet<Course>();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "id")
    private Integer id;
    @Column(name = "name")
    private String name;
    @ManyToMany
    @JoinTable(name = "Score", joinColumns = @JoinColumn(name = "student_id"), inverseJoinColumns = @JoinColumn(name = "course_id"))
    public Set<Course> getCourses() {
        return courses;
    }

    public void setCourses(Set<Course> courses) {
        this.courses = courses;
    }


    public Student(){}

    public Student(Integer id, String name, Course course_id) {
        this.id = id;
        this.name = name;
        this.course_id = course_id;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", course_id=" + course_id +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Course getCourse_id() {
        return course_id;
    }

    public void setCourse_id(Course course_id) {
        this.course_id = course_id;
    }
}