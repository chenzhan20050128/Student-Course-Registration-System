<template>
    <div id="app">
      <h1>学生选课系统</h1>
      <div>
        <label for="studentName">学生姓名:</label>
        <input type="text" id="studentName" v-model="studentName">
      </div>
      <div>
        <label for="course">选择课程:</label>
        <select id="course" v-model="selectedCourse">
          <option v-for="course in courses" :key="course.id" :value="course.name">{{ course.name }}</option>
        </select>
      </div>
      <button @click="enroll">选课</button>
      <div v-if="enrolledCourses.length">
        <h2>已选课程</h2>
        <ul>
          <li v-for="course in enrolledCourses" :key="course.id">{{ course.name }}</li>
        </ul>
      </div>
    </div>
  </template>
  
  <script lang="ts">
  import { defineComponent } from 'vue';
  
  export default defineComponent({
    data() {
      return {
        studentName: '',
        selectedCourse: '',
        courses: [
          { id: 1, name: '数学' },
          { id: 2, name: '英语' },
          { id: 3, name: '计算机科学' }
        ],
        enrolledCourses: []
      };
    },
    methods: {
      enroll() {
        if (this.selectedCourse && !this.enrolledCourses.find(course => course.name === this.selectedCourse)) {
          const course = this.courses.find(course => course.name === this.selectedCourse);
          if (course) {
            this.enrolledCourses.push(course);
          }
        }
      }
    }
  });
  </script>
  
  <style lang="css">
  #app {
    font-family: Avenir, Helvetica, Arial, sans-serif;
    text-align: center;
    color: #2c3e50;
    margin-top: 60px;
  }
  </style>