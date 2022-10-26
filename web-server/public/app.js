const TodoItem = {
  template: "<li>{{ todo.text }}</li>",
  props: ["todo"],
};

const SearchBar = {
  template: `
      <form @submit="handleSubmit" class="search-bar">
        <input v-model='search' placeholder="Search" />
        <button></button>
      </form>`,
  methods: {
    handlerSubmit() {

    }
  },
  data() {
    return { search: '' }
  }
};

const PageHeader = {
  components: {
    SearchBar,
  },
  template: `
    <header class="page-header">
      <a class="logo" href="/">Cameras</a>
      <search-bar v-if="showSearchBar"></search-bar>
      <div></div>
    </header>
  `,
  props: ["showSearchBar"],
};

const HomePage = {
  components: {
    SearchBar,
  },
  template: `
    <section class="home-page">
      <search-bar></search-bar>
    </section>
  `,
};

const Counter = {
  components: {
    SearchBar,
    PageHeader,
    HomePage,
  },
  data() {
    return {
      showSearchBar: false,
      // counter: 0,
      // text: "",
      // todos: [
      //   { text: "Learn JavaScript", id: Math.random() },
      //   { text: "Learn Vue", id: Math.random() },
      //   { text: "Build something awesome", id: Math.random() },
      // ],
    };
  },
  mounted() {
    setInterval(() => {
      this.counter++;
    }, 1000);
  },
  created() {},
  methods: {
    addTodo() {
      this.todos.push({ text: this.text, id: Math.random() });
    },
  },
};

const vm = Vue.createApp(Counter).mount("#app");
