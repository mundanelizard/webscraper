
const HomeComponent = {
    data() {
        return {
            loadingGenres: false,
            genres: [],
            loadingAuthors: false,
            authors: [],
            search: '',
        }
    },
    methods: {
        async handleSearch() {
            window.location.href = '#/search/?search=' + encodeURIComponent(this.search)
        }
    },
    mounted() {
        this.loadingGenres = true;

        fetch("/api/genres")
            .then(res => res.json())
            .then(genres => {

                this.genres = genres
                this.loadingGenres = false;
            });

        this.loadingAuthors = true;

        fetch("/api/authors")
            .then(res => res.json())
            .then(authors => {
                this.authors = authors;
                this.loadingAuthors = false;
            })
    },
    template: `
    <div class="home">
      <h1 class="logo"><a href="/">Book Checker</a></h1>
      <form @submit.prevent="handleSearch">
        <input 
            v-model="search"  
            placeholder="Search" 
            />
        <button>Search</button>
      </form>
    </div>
    `
}

export { HomeComponent };
