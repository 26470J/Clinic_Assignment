import React, { useState } from 'react'
import ClinicCard from './ClinicCard'

const booksFromDb = [
  {
    code: "S1",
    name: 'Full Stack Development',
    price: 100
  },
  {
    code: "S2",
    name: 'Software Development',
    price: 190
  },
  {
    code: "S3",
    name: 'Software Engineering',
    price: 50
  }
]

function ClinicBooks() {
  const [books, setBooks] = useState(booksFromDb)

  return (
    <div>
      <h2>List of Books</h2>
      <ul>
        {
          books.map(book => <ClinicCard bookInfo={book} />)
        }
      </ul>
      <div>
        <h3>The retail price for {books.length} books is {books.reduce((a, b) => a + b.price, 0)}</h3>
      </div>
    </div>
  )
}

export default ClinicBooks
