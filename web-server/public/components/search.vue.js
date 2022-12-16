
const SearchComponent = {
    data() {
        return {
            search: '',

            keyword: '',
            data: [],
            total: 0,
            nextBatch: 0,
            totalBatch: 0,

            loading: false,
            size: 10,
            batch: 1,
        }
    },
    methods: {
        async handleSearch(batch) {
            this.loading = true;

            if (typeof batch === "number") {
                this.batch = batch;
                this.search = this.keyword
            } else {
                window.location.href = '#/search/?search=' + encodeURIComponent(this.search)
            }

            fetch(`/api/books?batch=${this.batch}&size=${this.size}&search=${this.search}`)
                .then(res => res.json())
                .then(({ data, total, nextBatch, totalBatch, batch, size } )=> {
                    this.loading = false;

                    this.data = data;
                    this.total= total;
                    this.size = size;
                    this.totalBatch = totalBatch;
                    this.nextBatch = nextBatch;
                    this.batch = batch;

                    if (this.keyword === this.search) {
                        this.search = '';
                    }
                })
        }
    },
    mounted() {
        this.search = decodeURIComponent(window.location.hash.replace('#/search/?search=', ''))
        this.handleSearch()
    },
    template: `
    <div class="search">
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
        <div class="info">
            <h3 v-if="data.length !== 0 && !loading">Showing {{total}} results for \"{{keyword}}\"</h3>
            <h3 v-else>Search Not Found</h3>
        </div>
         
        <section class="books" v-if="!loading">
            <div class="book" v-for="book in data">
                <a :href="'/#/book/' + book.isbn">
                    <div class="image">
                        <img :src="book.image" />
                    </div>
                    <h3>{{book.title}}</h3>
                    <em>ISBN: {{book.isbn}}</em>
                </a>
            </div>
        </section>
        
        <div v-else>Loading...</div>
        
        <section class="pagination">
            <button 
                @click="handleSearch(batch - 1)"
                v-if="batch != 1"
                >Previous</button>
            <button 
                @click="handleSearch(n)"
                v-for="n in totalBatch" 
                :style="batch == n ? 'background-color: black; color: white' : ''"
                >{{ n }}</button>
            <button 
                @click="handleSearch(nextBatch)"
                v-if="totalBatch !== batch && totalBatch != 0"
                >Next</button>
        </section>
    </div>
    `
}

export { SearchComponent };
