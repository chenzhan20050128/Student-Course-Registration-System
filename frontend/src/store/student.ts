import { defineStore } from 'pinia';

export const useStudentStore = defineStore('student', {
  state: () => ({
    student: {
      id: null,
      sname: '',
      sex: '',
      age: null,
      major: '',
      college: '',
      simage: '',
      password: '',
    },
  }),
  actions: {
    setStudent(student:any) {
      this.student = student;
    },
  },
});