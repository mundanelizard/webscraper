
const BookComponent = {
    data() {
        return {
            loading: false,

            isbn: "",
            search: "",

            authors: [],
            description: '',
            genres: [],
            image: "",
            prices: [],
            title: "",
            formattedGenres: '',
            formattedAuthors: '',
        }
    },
    methods: {
        async loadBook() {
            this.loading = true;

            fetch(`/api/books/${this.isbn}`)
                .then(res => res.json())
                .then((result) => {
                    this.loading = false;

                    this.authors = result.authors;
                    this.description = result.description;
                    this.genres = result.genres;
                    this.image = result.image;
                    this.isbn = result.isbn;
                    this.prices = result.prices;
                    this.title = result.title;

                    this.formattedGenres = result.genres.map(({ title }) => title).join(", ");
                    this.formattedAuthors = result.authors.map(({ name }) => name).join(", ");
                })
        },
        async handleSearch() {
            window.location.href = '/?search=' + encodeURIComponent(this.search)
        }
    },
    mounted() {
        this.isbn = decodeURIComponent(window.location.hash.replace('#/book/', ''))
        this.loadBook()
    },
    template: `
    <div class="details">
        <header class="header">
            <div class="logo"><a href="/">Book Comparison</a></div>
            <form @submit.prevent="handleSearch">
                <input 
                    v-model="search"  
                    placeholder="Search" 
                    />
                <button>Search</button>
            </form>
        </header>
        
        <main>
            <section class="image">
                <img :src="image" height="400" width="400" />
            </section>
            
            <section class="info">
                <h1>{{title}}</h1>
                <article class="description" v-html="description"></article>
                <div class="table">
                   <b>Genres: </b>
                   <span >{{formattedGenres}}</span>
                </div>
                <div class="table">
                    <b>Authors: </b>
                   <span>{{formattedAuthors}}</span>
                </div>
                <div class="table">
                    <b>ISBN: </b>
                   <span>{{isbn}}</span>
                </div>
            </section>
            
            <section class="comparison">
                <div>
                    <div class="price" v-for="price in prices">
                        <a :href="price.url" target="_blank">
                           <div class="provider">{{price.provider}}</div>
                           <div class="cost"><em>{{price.price}}</em></div>
                           <i class="time">{{new Date(price.updated_at).toLocaleString()}}</i>
                        </a>
                    </div>
                </div>
            </section>
          
        </main>
    </div>
    `
}

export { BookComponent };
