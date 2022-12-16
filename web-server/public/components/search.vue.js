
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
            size: 20,
            batch: 1,
        }
    },
    methods: {
        async handleSearch(batch) {
            this.loading = true;

            if (typeof batch != "undefined") {
                this.batch = batch;
                this.search = this.keyword
            } else {
                this.keyword = this.search;
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
            <div>
                <h3>Showing {{total}} results for \"{{keyword}}\"</h3>
            </div>
        </header>
         
        <section v-if="!loading">
            <div v-for="book in data">
                <a :href="'/#/book/' + book.isbn">
                    <img :src="book.image" height="200" width="200" />
                    <h3>{{book.title}}</h3>
                    <em>{{book.isbn}}</em>
                </a>
            </div>
        </section>
        
        <div v-else>Loading...</div>
        
        <section>
            <button 
                @click="handleSearch(batch - 1)"
                v-if="totalBatch != batch && batch != 1"
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
