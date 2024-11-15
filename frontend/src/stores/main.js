// store.js (Pinia)
import { defineStore } from 'pinia';
import axios from 'axios';

export const useDataStore = defineStore('dataStore', {
  state: () => ({
    apiData: null,
  }),
  actions: {
    fetchData() {
      axios.get('http://careerfit.com')
        .then(response => {
          this.apiData = response.data;
        })
        .catch(error => {
          console.error(error);
        });
    },
  },
});
