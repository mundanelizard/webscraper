
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

                    console.log(this.prices);
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
    <div>
        <header>
            <h1 class="logo"><a href="/">Book Checker</a></h1>
            <form @submit.prevent="handleSearch">
                <input 
                    v-model="search"  
                    placeholder="Search" 
                    />
                <button>Search</button>
            </form>
            <nav>
                <h3>ISBN: {{isbn}}</h3>
            </nav>
        </header>
        
        <main>
            <div>
                <img :src="image" height="400" width="400" />
                <h2>{{title}}</h2>
                <div>
                    <h4>Comparisons</h4>
                    <div>
                        <div v-for="price in prices">
                            <a :href="price.url" target="_blank">
                               <div>{{price.provider}}</div>
                               <div><em>{{price.price}}</em></div>
                               <i>{{price.updated_at}}</i>
                            </a>
                        </div>
                    </div>
                </div>
            </div>
            
            <section>
                <article v-html="description"></article>
                <div>
                   <h3>Genres</h3>
                   <span >{{formattedGenres}}</span>
                </div>
                <div>
                    <h3>Authors</h3>
                   <span>{{formattedAuthors}}</span>
                </div>
            </section>
        </main>
    </div>
    `
}

export { BookComponent };
