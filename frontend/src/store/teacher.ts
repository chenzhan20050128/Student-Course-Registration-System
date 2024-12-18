import { defineStore } from 'pinia';

export const useTeacherStore = defineStore('teacher', {
  state: () => ({
    teacher: {
      id: null,
      tname: '',
      sex: '',
      age: null,
      major: '',
      college: '',
      timage: '',
      password: '',
    },
  }),
  actions: {
    setTeacher(teacher) {
      this.teacher = teacher;
    },
    clearTeacher() {
      this.teacher = {
        id: null,
        tname: '',
        sex: '',
        age: null,
        major: '',
        college: '',
        timage: '',
        password: '',
      };
    },
  },
  persist: {
    enabled: true,
    strategies: [
      {
        key: 'teacher',
        storage: localStorage,
      },
    ],
  },
});