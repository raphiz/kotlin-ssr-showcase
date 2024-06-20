import { Controller } from "@hotwired/stimulus"

export default class PasswordVisibility extends Controller<HTMLElement> {
  class: string
  hidden: boolean
  static targets = ["input", "icon"]
  static classes = ["hidden"]

  declare readonly hasHiddenClass: boolean
  declare readonly hiddenClass: string
  declare readonly inputTarget: HTMLInputElement
  declare readonly iconTargets: HTMLElement[]

  connect(): void {
    this.hidden = this.inputTarget.type === "password"
    this.class = this.hasHiddenClass ? this.hiddenClass : "hidden"
  }

  toggle(e: Event): void {
    e.preventDefault()
    this.inputTarget.type = this.hidden ? "text" : "password"
    this.hidden = !this.hidden

    this.iconTargets.forEach((icon) => icon.classList.toggle(this.class))
  }
}


// Note: Decorators have changed in TS5: https://devblogs.microsoft.com/typescript/announcing-typescript-5-0/#decorators
// import { Controller } from "@hotwired/stimulus"
// import { Class, Target, TypedController } from "@vytant/stimulus-decorators";

// @TypedController
// export default class PasswordVisibility extends Controller<HTMLElement> {
//   class: string
//   hidden: boolean

//   @Class readonly hasHiddenClass: boolean
//   @Class readonly hiddenClass: string
//   @Target readonly inputTarget: HTMLInputElement
//   @Target readonly iconTargets: HTMLElement[]

//   connect(): void {
//     console.log(this.inputTarget);
//     this.hidden = this.inputTarget.type === "password"
//     this.class = this.hasHiddenClass ? this.hiddenClass : "hidden"
//   }

//   toggle(e: Event): void {
//     e.preventDefault()
//     this.inputTarget.type = this.hidden ? "text" : "password"
//     this.hidden = !this.hidden

//     this.iconTargets.forEach((icon) => icon.classList.toggle(this.class))
//   }
// }